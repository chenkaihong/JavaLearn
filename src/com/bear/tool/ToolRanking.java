package com.bear.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bear.tool.ToolRanking.Rank;

/**
 * 排行榜分数只增不减
 * @author Administrator
 *
 */
public class ToolRanking implements Iterable<Rank> {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock  rLock = lock.readLock();
	private final WriteLock wLock = lock.writeLock();
	
	private final int topSize;
	private final int capacity;
	private volatile List<Rank> topList;
	private Map<Integer, Rank> mapWithPlayerID;		// Map<PlayerID, Rank>
	private Map<Integer, Rank> mapWithPosition;		// Map<Position, Rank>
	private final Rank headRank;
	private final Rank tailRank;
	
	public ToolRanking(int topSize, int capacity){
		mapWithPlayerID = new HashMap<Integer, Rank>();
		mapWithPosition = new HashMap<Integer, Rank>();
		headRank = new Rank(-10, -10);
		tailRank = new Rank(-20, -20);
		entryConnection(headRank, tailRank);
		this.capacity = capacity;
		this.topSize = topSize;
		topList = new ArrayList<Rank>(topSize);
	}
	/**
	 * 默认排行榜容量为10000, 如果需要更大, 需要自行设置
	 * @param topSize
	 */
	public ToolRanking(int topSize){
		this(topSize, 10000);
	}
	
	public void load(Map<Integer, Integer> map){
		// 排行榜在日常工作中,只会顺序递增,以及少量的排位递增,所以使用TreeMap不怎么划算,只有在初始化时使用比较好
		TreeSet<Rank> tree = new TreeSet<Rank>(new Compare());
		for(Entry<Integer, Integer> entry : map.entrySet()){
			tree.add(new Rank(entry.getKey(), entry.getValue()));
		}
		int position = 1;
		
		wLock.lock();
		try{
			Rank temp = headRank;
			for(Rank rank : tree){
				
				if(position > capacity){
					break;
				}
				
				rank.position = position;
				entryNextInsert(rank, temp);
				mapWithPlayerID.put(rank.playerID, rank);
				mapWithPosition.put(position, rank);
				
				position++;
				temp = rank;
			}
		}finally{
			wLock.unlock();
		}
		tree.clear();
		topList = newTop();
	}
	
	public void mark(int playerID, int score){
		Rank thiss = null;
		wLock.lock();
		try{
			thiss = mapWithPlayerID.get(playerID);
			// 判断rank是否已经进入过链表, 若进入过, 则进行重新排列, 若没有进入过, 则新建并添加到链表末尾
			if(thiss == null){
				
				if(isAbandon(score)){			// 如果排行榜容量到达上限,并输入的score小于排行榜最小值,则做舍弃处理
					return;
				}
				
				thiss = new Rank(playerID, score);
				mapWithPlayerID.put(playerID, thiss);
				entryPrevInsert(thiss, tailRank);			// 将新增的元素添加到末尾
				thiss.position = thiss.prev.position + 1;
			}else{
				thiss.score = score;
			}
			// 这个地方使用TreeMap感觉有点浪费,因为排行榜日常使用时排名变动不大,在初始化的时候再使用TreeMap
			Rank higher = getHigherRank(thiss);
			if(higher == null){
				higher = headRank;
			}
			// 判断元素是否需要移动链表位置, 若需要移动, 则先将移动路径上所有Rank的position+1, 然后再进行元素移动并重新赋值position
			// 此处已经排除了降分的可能性, 如果要适应降分操作,则需要在这里额外扩展
			if(higher != thiss.prev){
				incPosition(higher, thiss);
				entryMove(higher, thiss);
			}
			// 若已经超出了排行榜上限,则将最后一名进行舍弃
			if(mapWithPlayerID.size() > capacity){
				Rank temp = tailRank.prev;
				entryConnection(temp.prev, tailRank);
				
				mapWithPlayerID.remove(temp.playerID);
				mapWithPosition.remove(temp.position);
				topList.remove(temp);
			}
		}finally{
			wLock.unlock();
		}
		
		// 当移动名次涉及到top, 则重新装载top名单
		if(thiss != null && thiss.position <= topSize){
			topList = newTop();
		}
	}
	
	public Rank getRankByPlayerID(int playerID){
		rLock.lock();
		Rank rank = null;
		try{
			rank = mapWithPlayerID.get(playerID);
		}finally{
			rLock.unlock();
		}
		return rank;
	}
	public Rank getRankByPosition(int position){
		rLock.lock();
		Rank rank = null;
		try{
			rank = mapWithPosition.get(position);
		}finally{
			rLock.unlock();
		}
		return rank;
	}
	public List<Rank> getTop(){
		return Collections.unmodifiableList(topList);
	}
	
	/**
	 * 判断进入的score是否比排行榜中最小的都小, 若排行榜容量没有达上限则不做舍弃
	 * @return
	 */
	private boolean isAbandon(int score){
		
		if((mapWithPlayerID.size()+1) <= capacity){
			return false;
		}
		
		return tailRank.prev.score >= score;
	}
	
	/**
	 * 映射出TopList
	 */
	private List<Rank> newTop(){
		Rank temp = headRank.next;
		List<Rank> tempList = new ArrayList<Rank>();
		for(int i = 0; i < topSize; i++){
			if(temp == tailRank){
				break;
			}
			tempList.add(temp);
			temp = temp.next;
		}
		return tempList;
	}
	
	/**
	 * 获取元素的prev Rank
	 * @param thiss
	 * @return
	 */
	private Rank getHigherRank(Rank thiss){
		Rank temp = thiss.prev;
		while(temp != headRank){
			if(temp.score >= thiss.score){
				return temp;
			}
			temp = temp.prev;
		}
		return null;
	}
	
	/**
	 * 将移动路径上所有Rank的position+1
	 * @param newHigher
	 * @param thiss
	 */
	private void incPosition(Rank higher, Rank thiss){
		Rank temp = higher.next;
		int position = higher.position + 2;
		// 排位递增1
		while(temp != thiss){
			temp.position = position;
			mapWithPosition.put(position, temp);
			temp = temp.next;
			position++;
		}
	}
	
	/**
	 * 移动需要提升position的元素
	 * @param newHigher
	 * @param thiss
	 */
	private void entryMove(Rank higher, Rank thiss){
		// 将原来的上下元素相连
		entryConnection(thiss.prev, thiss.next);
		// 安插在正确的位置上
		entryNextInsert(thiss, higher);
		
		// 将插入的元素进行序号调整
		int position = higher.position+1;
		thiss.position = position;
		mapWithPosition.put(position, thiss);
	}
	
	/**
	 * 将连个Rank相连
	 * @param high
	 * @param low
	 */
	private void entryConnection(Rank high, Rank low){
		high.next = low;
		low.prev = high;
	}
	
	/**
	 * 将元素插入到已知元素之前
	 * @param thiss
	 * @param next
	 */
	private void entryPrevInsert(Rank thiss, Rank next){
		thiss.prev = next.prev;
		thiss.next = next;
		next.prev.next = thiss;
		next.prev = thiss;
	}
	
	/**
	 * 将元素插入到已知元素之后
	 * @param thiss
	 * @param next
	 */
	private void entryNextInsert(Rank thiss, Rank Prev){
		thiss.prev = Prev;
		thiss.next = Prev.next;
		Prev.next.prev = thiss;
		Prev.next = thiss;
	}
	
	/**
	 * 迭代器
	 */
	@Override
	public Iterator<Rank> iterator() {
		return new RankIterator();
	}
	class RankIterator implements Iterator<Rank>{
		private Rank temp;
		public RankIterator(){
			temp = headRank;
		}

		@Override
		public boolean hasNext() {
			return temp.next != tailRank;
		}
		@Override
		public Rank next() {
			temp = temp.next;
			return temp;
		}
		@Override
		public void remove() {
			throw new RuntimeException("@@@ Ranking is no support remove...");
		}
	}
	
	/**
	 * 按照score逆序排序, 并按照时间正序排序
	 * @author Administrator
	 *
	 */
	class Compare implements Comparator<Rank>{
		public int compare(Rank o1, Rank o2) {
			int result = 0;
			if(o1.score > o2.score){
				result = -1;
			}else if(o1.score < o2.score){
				result = 1;
			}else{
				if(o1.lastTime >= o2.lastTime){
					result = 1;
				}else if(o1.lastTime < o2.lastTime){
					result = -1;
				}
			}
			return result;
		}
	}
	
	public class Rank{
		private int playerID;
		private int score;
		private int position;
		private long lastTime;
		
		private Rank prev;
		private Rank next;
		
		public Rank(int playerID, int score) {
			this.playerID = playerID;
			this.score = score;
			this.lastTime = System.currentTimeMillis();
			this.position = 0;
			this.prev = null;
			this.next = null;
		}

		@Override
		public String toString() {
			return String.format("%s - %s - %s", playerID, score, position);
		}

		public int getPlayerID() {
			return playerID;
		}
		public int getScore() {
			return score;
		}
		public int getPosition() {
			return position;
		}
		public long getLastTime() {
			return lastTime;
		}
	}
}
package com.bear.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ToolAction {
	
	private final Map<String,TimePointList> timePointTempMap = new ConcurrentHashMap<String,TimePointList>();
	private final static Pattern pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):([0-5][0-9])");
	private final static ToolAction me = new ToolAction();
	
	private ToolAction(){}
	public static ToolAction me(){
		return me;
	}
	
	/**
	 * 通过上次刷新时间判断现在是否可以进行刷新
	 * @param name
	 * @param lastFlushTime
	 * @return
	 */
	public boolean canFlush(String name, long lastFlushTime){
		TimePointList timePoint = timePointTempMap.get(name);
		return lastFlushTime < timePoint.indexTime;
	}
	
	/**
	 * 注册需要控制刷新时间的项目
	 * @param name
	 * @param timePointTempList String的格式必须要 HH:ss
	 */
	public void register(String name, List<String> timePointTempList){
		if(timePointTempMap.containsKey(name)){
			throw new RuntimeException("@@@ Same timetimePointList point name! name: " + name);
		}
		if(ToolList.isEmpty(timePointTempList)){
			throw new RuntimeException("@@@ TimePointList can not be empty");
		}
		
		for(String timePoint : timePointTempList){
			if(!pattern.matcher(timePoint).matches()){
				throw new RuntimeException("@@@ TimePoint must be like 'HH:ss'! now is " + timePoint);
			}
		}
		
		// 排序算法,也顺便去重
		timePointTempList = sortAndDelRepeat(timePointTempList);
		
		TimePointList timePointList = new TimePointList(timePointTempList);
		timePointTempMap.put(name, timePointList);
	}
	private List<String> sortAndDelRepeat(List<String> list){
		Map<Long, String> map = new HashMap<Long, String>();
		List<Long> sortList = new LinkedList<Long>();
		for(String s : list){
			long temp = stringTolong(s);
			map.put(temp, s);
			sortList.add(temp);
		}
		Collections.sort(sortList);
		List<String> returnList = new ArrayList<String>();
		for(long l : sortList){
			String s = map.get(l);
			// 去重
			if(returnList.contains(s)){
				continue;
			}
			returnList.add(s);
		}
		return returnList;
	}
	private long stringTolong(String temp){
		String[] hourAndminute = temp.split(":");
		long l = Long.parseLong(hourAndminute[0])*3600L*1000L + Long.parseLong(hourAndminute[1])*60L*1000L;
		return l;
	}
	
	/**
	 * 重算刷新时间和下次刷新时间
	 */
	public void flush(){
		for(Entry<String, TimePointList> entry : timePointTempMap.entrySet()){
			long now = System.currentTimeMillis();
			
			synchronized (entry) {
				TimePointList timePoint = entry.getValue();
				while(now < timePoint.indexTime){
					timePoint.pull();
				}
				while(now > timePoint.nextIndexTime){
					timePoint.push();
				}
			}
		}
	}
	
	private class TimePointList extends LinkedList<String>{
		private static final long serialVersionUID = 1L;
		private volatile int index;
		private volatile long indexTime;
		private volatile long nextIndexTime;
		
		private TimePointList(Collection<? extends String> list){
			super(list);
			index = 0;
			indexTime = System.currentTimeMillis();
			nextIndexTime = indexTime;
			push();
		}
		
		private void push(){
			// 当前时间戳计算
			int nextIndex = nextView();
			String[] hourAndminute = get(nextIndex).split(":");
			int hour = Integer.parseInt(hourAndminute[0]);
			int minute = Integer.parseInt(hourAndminute[1]);
			boolean isOtherDay = false;
			if(index >= nextIndex){
				isOtherDay = true;
			}
			long nextLong = pushTime(indexTime, hour, minute, isOtherDay);
			index = nextIndex;
			indexTime = nextLong;
			// 下一时间戳计算
			nextIndex = nextView();
			hourAndminute = get(nextIndex).split(":");
			hour = Integer.parseInt(hourAndminute[0]);
			minute = Integer.parseInt(hourAndminute[1]);
			isOtherDay = false;
			if(index >= nextIndex){
				isOtherDay = true;
			}
			nextLong = pushTime(indexTime, hour, minute, isOtherDay);
			nextIndexTime = nextLong;
		}
		private void pull(){
			// 当前时间戳计算
			int backIndex = backView();
			String[] hourAndminute = get(backIndex).split(":");
			int hour = Integer.parseInt(hourAndminute[0]);
			int minute = Integer.parseInt(hourAndminute[1]);
			boolean isOtherDay = false;
			if(index <= backIndex){
				isOtherDay = true;
			}
			long backLong = pullTime(indexTime, hour, minute, isOtherDay);
			index = backIndex;
			indexTime = backLong;
			// 下一时间戳计算
			int nextIndex = nextView();
			hourAndminute = get(nextIndex).split(":");
			hour = Integer.parseInt(hourAndminute[0]);
			minute = Integer.parseInt(hourAndminute[1]);
			isOtherDay = false;
			if(index >= nextIndex){
				isOtherDay = true;
			}
			long nextLong = pushTime(indexTime, hour, minute, isOtherDay);
			nextIndexTime = nextLong;
		}
		
		private int nextView(){
			int indexTemp = index;
			indexTemp++;
			if(indexTemp >= size()){
				indexTemp = 0;
			}
			return indexTemp;
		}
		private int backView(){
			int indexTemp = index;
			indexTemp--;
			if(indexTemp < 0){
				indexTemp = size()-1;
			}
			return indexTemp;
		}
		
		private long pushTime(long time, int toHour, int toMinute, boolean isOtherDay){
			Calendar timeTemp = Calendar.getInstance();
			timeTemp.setTimeInMillis(time);
			if(isOtherDay){
				timeTemp.add(Calendar.DAY_OF_MONTH, 1);
			}
			timeTemp = ToolTime.fillCalendar(timeTemp, toHour, toMinute, 0, 0);
			return timeTemp.getTimeInMillis();
		}
		private long pullTime(long time, int toHour, int toMinute, boolean isOtherDay){
			Calendar timeTemp = Calendar.getInstance();
			timeTemp.setTimeInMillis(time);
			if(isOtherDay){
				timeTemp.add(Calendar.DAY_OF_MONTH, -1);
			}
			timeTemp = ToolTime.fillCalendar(timeTemp, toHour, toMinute, 0, 0);
			return timeTemp.getTimeInMillis();
		}
	}
	
	public static void main(String[] args) {
		ToolAction.me().register("Test1", Arrays.asList("12:00", "09:00", "18:00"));
		ToolAction.me().register("Test2", Arrays.asList("09:00"));
		ToolAction.me().register("Test3", Arrays.asList("09:00","09:00","09:00"));
		ToolAction.me().flush();
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					ToolAction.me().flush();
					TimePointList temp = ToolAction.me().timePointTempMap.get("Test1");
					SimpleDateFormat sDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
					Calendar tempTime = Calendar.getInstance();
					tempTime.setTimeInMillis(temp.indexTime);
					System.out.println(sDateFormat.format(tempTime.getTime()));
					tempTime.setTimeInMillis(temp.nextIndexTime);
					System.out.println(sDateFormat.format(tempTime.getTime()));
					System.out.println();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}

package com.bear.demo;

import java.util.HashMap;
import java.util.Map;

import com.bear.tool.ToolRanking;
import com.bear.tool.ToolRanking.Rank;

public class RankDemo {

	public static void main(String[] args) {
		int top = 20;
		int size = 100;
		ToolRanking rank = new ToolRanking(top, size);
		
		Map<Integer,Integer> map = new HashMap<Integer, Integer>();
		map.put(12000000, 10);
		map.put(12000001, 20);
		map.put(12000002, 10);
		map.put(12000003, 100);
		map.put(12000004, 5);
		
		rank.load(map);
		
		for(Rank r : rank.getTop()){
			System.out.println(r);
		}
		
		Rank temp1 = rank.getRankByPlayerID(12000003);
		Rank temp2 = rank.getRankByPosition(1);
		
		System.out.println(temp1 == temp2);
	}
}

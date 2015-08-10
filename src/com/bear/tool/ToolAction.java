package com.bear.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class ToolAction {
	
	private Map<String,TimePointList> timePointTempMap = new HashMap<String,TimePointList>();
	private Map<String,Long> timePointMap = new ConcurrentHashMap<String,Long>();
	private Pattern pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):([0-5][0-9])");
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:ss");
	private static ToolAction me = new ToolAction();
	
	private ToolAction(){}
	public static ToolAction me(){
		return me;
	}
	
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
		TimePointList timePointList = new TimePointList(timePointTempList);
		
		// 排序算法
		
		timePointTempMap.put(name, timePointList);
	}
	
	public void reload(){
		for(Entry<String, TimePointList> entry : timePointTempMap.entrySet()){
			long now = System.currentTimeMillis();
			String name = entry.getKey();
			TimePointList timePoint = entry.getValue();
			
			while(timePoint.getNextTimePoint()<now){
				timePoint.next();
			}
		}
	}
	
	public static void main(String[] args) {
		
	}
	
}

class TimePointList extends LinkedList<String>{
	private static final long serialVersionUID = 1L;
	private int index;
	private AtomicLong nowTime;
	
	public TimePointList(Collection<? extends String> list){
		super(list);
		index = 0 ;
		String[] hourAndminute = get(index).split(":");
		Calendar now = Calendar.getInstance();
		ToolTime.fillCalendar(now, 
							  Integer.parseInt(hourAndminute[0]), 
							  Integer.parseInt(hourAndminute[1]), 0, 0);
		nowTime = new AtomicLong(now.getTimeInMillis());
	}
	
	public void next(){
		String[] hourAndminuteA = 
		index = getNextView();
	}
	public void front(){
		index = getFrontView();
	}
	
	private int getNextView(){
		int tempIndex = index;
		tempIndex++;
		if(tempIndex > (size()-1)){
			tempIndex = 0;
		}
		return tempIndex;
	}
	private int getFrontView(){
		int tempIndex = index;
		tempIndex--;
		if(tempIndex < 0){
			tempIndex = size()-1;
		}
		return tempIndex;
	}
	
	private long diffLong(long hourA, long minuteA, long hourB, long minuteB){
		return Math.abs(hourA-hourB)*3600*1000 + Math.abs(minuteA-minuteB)*60*1000;
	}
}

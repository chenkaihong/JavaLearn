package com.bear.demo.LBS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bear.tool.ToolLBS;

public class LBSDemo {

	private static final Map<String, List<Teacher>> teacherMap;
	
	static{
		teacherMap = new HashMap<String, List<Teacher>>();
		
		double lat = 113.292882;
		double lon = 23.142665;
		
		Teacher teacher1 = new Teacher(1, "Jack1", lat, lon);
		teacher1.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.293125;
		lon = 23.143072;
		Teacher teacher2 = new Teacher(2, "Jack2", lat, lon);
		teacher2.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.288615;
		lon = 23.145655;
		Teacher teacher3 = new Teacher(3, "Jack3", lat, lon);
		teacher3.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.292442;
		lon = 23.140837;
		Teacher teacher4 = new Teacher(4, "Jack4", lat, lon);
		teacher4.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.284609;
		lon = 23.137746;
		Teacher teacher5 = new Teacher(5, "Jack5", lat, lon);
		teacher5.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.28177;
		lon = 23.132878;
		Teacher teacher6 = new Teacher(5, "Jack6", lat, lon);
		teacher6.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		lat = 113.240466;
		lon = 23.092709;
		Teacher teacher7 = new Teacher(5, "Jack7", lat, lon);
		teacher7.setGeoHash(ToolLBS.encodeGeoHash(lat, lon));
		
		insert(teacherMap, teacher1);
		insert(teacherMap, teacher2);
		insert(teacherMap, teacher3);
		insert(teacherMap, teacher4);
		insert(teacherMap, teacher5);
		insert(teacherMap, teacher6);
		insert(teacherMap, teacher7);
	}
	
	private static void insert(Map<String, List<Teacher>> teacherMap, Teacher teacher){
		String geoHash = teacher.getGeoHash();
		List<Teacher> teachers = teacherMap.get(geoHash);
		if(teachers == null){
			teachers = new ArrayList<Teacher>();
			teacherMap.put(geoHash, teachers);
		}
		teachers.add(teacher);
	}
	
	public static void main(String[] args) {
		double lat = 113.292909;
		double lon = 23.143736;
		
		String geoHash = ToolLBS.encodeGeoHash(lat, lon);
		List<Teacher> teachers = new ArrayList<Teacher>();
		List<Teacher> temp;
		for(String s : ToolLBS.getNeighbor(geoHash)){
			temp = teacherMap.get(s);
			if(temp != null && !temp.isEmpty()){
				teachers.addAll(temp);
			}
		}
		
		for(Teacher s : teachers){
			System.out.println(s.getName() + ": " + ToolLBS.getDistance(lon, lat, s.getLon(), s.getLat()));
		}
	}
}

package com.bear;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.bear.scan.Demos;
import com.bear.scan.Description;
import com.bear.tool.ToolClassFind;
import com.bear.tool.ToolList;
import com.bear.tool.ToolPath;

public class Scanner {

	public static void main(String[] args) throws URISyntaxException {
		
		final String desString = "[%s] %s";
		String classPath = ToolPath.getRootClassPath();
		List<Class<? extends Demos>> demoList = ToolClassFind.of(Demos.class, classPath).search();
		
		Map<String, List<String>> desMap = new TreeMap<String, List<String>>();
		for(char i = 65; i < 91; i++){
			desMap.put(i+"", new ArrayList<String>());
		}
		
		for(Class<?> cla : demoList){
			Description des = cla.getAnnotation(Description.class);
			if(des == null){
				throw new RuntimeException("Some class havn't description! class: " + cla.getSimpleName());
			}
			List<String> desList = desMap.get(des.sort());
			desList.add(String.format(desString, cla.getSimpleName(), des.description()));
		}
		
		for(Entry<String, List<String>> entryDes : desMap.entrySet()){
			String index = entryDes.getKey();
			List<String> desList = entryDes.getValue();
			
			if(!ToolList.isEmpty(desList)){
				System.out.println(index);
				for(String s : desList){
					System.out.println(s);
				}
				System.out.println();
			}
		}
	}
	
}

package com.bear.tool;

public class ToolString {
	
	public static boolean isEmpty(String temp){
		if(temp == null){
			return true;
		}
		temp = temp.trim();
		return temp.length() <= 0;
	}
	
}

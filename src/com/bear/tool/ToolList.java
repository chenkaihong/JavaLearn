package com.bear.tool;

import java.util.List;

public class ToolList {

	public static boolean isEmpty(List<?> list){
		return list == null || list.size() <= 0;
	}
	
	public static boolean isEmpty(Object[] objs){
		return objs == null || objs.length <= 0;
	}
	
}

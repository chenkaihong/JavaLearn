package com.gzyouai.hummingbird.engine.tryself5.util;

public class NullUtil {

	public static boolean anyNull(Object ...objs){
		for(Object obj: objs){
			if(obj == null){
				return true;
			}
		}
		return false;
	}
	
}

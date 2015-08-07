package com.bear.demo.protobuff.tryself5.handle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class HandleRegistered {

	// Map<cmdID, handle>
	private static Map<Integer, Class<? extends Handle>> handleClazzMap = new HashMap<Integer, Class<? extends Handle>>();
	private static Map<Integer, Handle> handleMap = new HashMap<Integer, Handle>();
	
	public static void register(int cmdID, Class<? extends Handle> handle) throws InstantiationException, IllegalAccessException{
		if(handleClazzMap.containsKey(cmdID)){
			throw new RuntimeException("@@@ Repeate cmdID! --> " + cmdID);
		}
		handleClazzMap.put(cmdID, handle);
		handleMap.put(cmdID, handle.newInstance());
	}
	
	public static Map<Integer, Class<? extends Handle>> getHandleClazzList(){
		if(handleMap == null){
			throw new RuntimeException("@@@ HandleMap is null");
		}
		return Collections.unmodifiableMap(handleClazzMap);
	}

	public static Handle getHandle(int cmdID) {
		return handleMap.get(cmdID);
	}
}

package com.bear.demo.nettyDemo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.bear.demo.nettyDemo.CmdBox.Cmd;
import com.google.protobuf.MessageLite;

public class MessageIndex{
	
	private Map<Integer, MessageProcess> processMap = new HashMap<Integer, MessageProcess>();
	private final static MessageIndex me = new MessageIndex();
	
	private MessageIndex(){}
	public static MessageIndex me(){
		return me;
	}
	
	public void isExist(){
		
	}
	
	/**
	 * 增加处理方案
	 * @param messageID
	 * @param process
	 * @throws Exception 
	 */
	public void addMessageProcess(int messageID, Class<?> systemClass, Method method) throws Exception{
		
		if(processMap.containsKey(messageID)){
			throw new RuntimeException("Have same messageID: " + messageID);
		}
		
		MessageProcess process = new MessageProcess(systemClass, method);
		processMap.put(messageID, process);
	}
	
	/**
	 * 进行任务分配
	 * @param messageID
	 * @param contentJson
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public MessageLite process(GameMessageTemp temp) throws Exception{
		
		Cmd cmd = temp.cmd;
		Object message = temp.message;
		
		int messageID = cmd.getMessage().getMessageId();
		MessageProcess process = processMap.get(messageID);
		MessageLite response = (MessageLite) process.method.invoke(process.processObj, message);
		return response;
	}
	
	class MessageProcess{
		public final Class<?> systemClass;
		public final Method method;
		public final Object processObj;
		
		public MessageProcess(Class<?> systemClass, Method method) throws Exception {
			this.systemClass = systemClass;
			this.method = method;
			this.processObj = systemClass.newInstance();
		}
	}
}

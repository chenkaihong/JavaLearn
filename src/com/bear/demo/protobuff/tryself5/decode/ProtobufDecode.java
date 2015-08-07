package com.bear.demo.protobuff.tryself5.decode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.protobuf.GeneratedMessage;

public class ProtobufDecode {
	
	private final static Logger log = Logger.getLogger(ProtobufDecode.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void addMethod(int cmd, Class clazz, Map<Integer,Method> methodMap, Map classMap, String methodName, Class ...clazzList) {
		try {
			Method m = clazz.getMethod(methodName, clazzList);
			
			if (methodMap.get(cmd) == null) {
				methodMap.put(cmd, m);
			}else{
				throw new RuntimeException("Repeat defined cmdID -> " + cmd);
			}
			
			if (classMap.get(cmd) == null) {
				classMap.put(cmd, clazz);
			}else{
				throw new RuntimeException("Repeat defind cmdID -> " + cmd);
			}
			
			log.info(String.format("Defind protobuf decode mothed: cmd[%d], class[%s]", cmd, clazz));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * protobuf解码
	 * 
	 * @throws Exception
	 * */
	@SuppressWarnings("unchecked")
	protected static <T> T decode(int cmd, Map<Integer, Method> methodMap, Map<Integer, Class<? extends GeneratedMessage>> clazzMap,  Object... args) throws Exception {
		Method decodeMethod = methodMap.get(cmd);
		Class<? extends GeneratedMessage> clazz = clazzMap.get(cmd);
		if (decodeMethod == null || clazz == null) {
			throw new Exception("找不到对应解码方法 - cmdID: " + cmd);
		}
		T req;
	
		try {
			req = (T) decodeMethod.invoke(clazz, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new Exception("解码异常 - cmdID: " + cmd, e);
		}
		return req;
	}
}

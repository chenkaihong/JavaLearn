package com.bear.demo.protobuff.tryself5.decode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.MessageLite.Builder;

public class ResponseDecode extends ProtobufDecode{

	private final static Map<Integer, Method> DECODE_RESPONSE_METHOD_MAP = new HashMap<Integer, Method>();
	private final static Map<Integer, Class<? extends GeneratedMessage>> DECODE_RESPONSE_CLASSES_MAP = new HashMap<Integer, Class<? extends GeneratedMessage>>();
	
	/**
	 * 注册response信息,用于通过cmdID提取出映射response类型
	 * @param cmd
	 * @param clazz
	 */
	public static void addResponse(int cmd, Class<? extends GeneratedMessage> clazz){
		addMethod(cmd, clazz, DECODE_RESPONSE_METHOD_MAP, DECODE_RESPONSE_CLASSES_MAP, "newBuilder");
	}
	
	/**
	 * 构造rsponse builder
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public static Builder decodeResponse(int cmd) throws Exception{
		return decode(cmd, DECODE_RESPONSE_METHOD_MAP, DECODE_RESPONSE_CLASSES_MAP);
	}
}

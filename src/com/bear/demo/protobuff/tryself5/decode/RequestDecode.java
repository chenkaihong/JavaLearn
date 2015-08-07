package com.bear.demo.protobuff.tryself5.decode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.MessageLite;

public class RequestDecode extends ProtobufDecode{

	private final static Map<Integer, Method> DECODE_REQUEST_METHOD_MAP = new HashMap<Integer, Method>();
	private final static Map<Integer, Class<? extends GeneratedMessage>> DECODE_REQUEST_CLASSES_MAP = new HashMap<Integer, Class<? extends GeneratedMessage>>();
	
	/**
	 * 注册request信息,用于通过cmdID提取出映射request类型,并将发送过来的数据解析到request类型中
	 * @param cmd
	 * @param clazz
	 */
	public static void addRequest(int cmd, Class<? extends GeneratedMessage> clazz){
		addMethod(cmd, clazz, DECODE_REQUEST_METHOD_MAP, DECODE_REQUEST_CLASSES_MAP, "parseFrom", new Class[] { byte[].class });
	}
	
	/**
	 * 通过data解码request信息
	 * @param cmd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static MessageLite decodeRequest(int cmd, byte[] data) throws Exception{
		return decode(cmd, DECODE_REQUEST_METHOD_MAP, DECODE_REQUEST_CLASSES_MAP, new Object[]{data});
	}
}

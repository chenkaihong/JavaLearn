package com.bear.demo.nettyDemo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bear.demo.nettyDemo.CmdBox.Cmd;
import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class GameProtobufDecoder extends MessageToMessageDecoder<Cmd>{
	
	private final static GameProtobufDecoder me = new GameProtobufDecoder();
	
	private final Map<Integer, Class<? extends MessageLite>> requestMap = new HashMap<Integer, Class<? extends MessageLite>>();
	private final Map<Integer, Method> requestMethodMap = new HashMap<Integer, Method>();
	
	private GameProtobufDecoder(){}
	public static GameProtobufDecoder me(){
		return me;
	}
	public void mapping(int messageID, Class<? extends MessageLite> clazz){
		requestMap.put(messageID, clazz);
		try {
			Method method = clazz.getDeclaredMethod("parseFrom", new Class[]{ byte[].class });
			requestMethodMap.put(messageID, method);
		} catch (Exception e) {
			throw new RuntimeException(String.format("@@@ Class parse error! - class: ", clazz.getName()), e);
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, Cmd cmd, List<Object> out) throws Exception {
		
		int messageID = cmd.getMessage().getMessageId();
		
		Method method = requestMethodMap.get(messageID);
		Class<?> clazz = requestMap.get(messageID);
		Object obj = method.invoke(clazz, cmd.getChildMessage().toByteArray());
		
		GameMessageTemp temp = new GameMessageTemp(cmd, obj);
		
		out.add(temp);
	}
}

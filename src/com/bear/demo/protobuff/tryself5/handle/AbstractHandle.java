package com.bear.demo.protobuff.tryself5.handle;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;


public abstract class AbstractHandle<R extends MessageLite, T extends Builder> implements Handle<R, T>{
	
	/**
	 *  加入点击统计等统一功能
	 */
	public static void pitCount(){
		
	}
}

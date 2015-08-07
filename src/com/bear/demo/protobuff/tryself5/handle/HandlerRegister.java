package com.bear.demo.protobuff.tryself5.handle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.protobuf.GeneratedMessage;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerRegister {
	Class<? extends GeneratedMessage> request();
	Class<? extends GeneratedMessage> response();
}

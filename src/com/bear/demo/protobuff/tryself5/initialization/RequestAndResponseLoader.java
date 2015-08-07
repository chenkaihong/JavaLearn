package com.gzyouai.hummingbird.engine.tryself5.initialization;

import java.util.Map.Entry;

import com.gzyouai.hummingbird.engine.tryself5.decode.RequestDecode;
import com.gzyouai.hummingbird.engine.tryself5.decode.ResponseDecode;
import com.gzyouai.hummingbird.engine.tryself5.handle.Handle;
import com.gzyouai.hummingbird.engine.tryself5.handle.HandleRegistered;
import com.gzyouai.hummingbird.engine.tryself5.handle.HandlerRegister;

public class RequestAndResponseLoader implements Loader{
	@SuppressWarnings("rawtypes")
	@Override
	public void load() throws Exception {
		for(Entry<Integer, Class<? extends Handle>> entry : HandleRegistered.getHandleClazzList().entrySet()){
			int cmdID = entry.getKey();
			Class<? extends Handle> clazz = entry.getValue();
			HandlerRegister handler = clazz.getAnnotation(HandlerRegister.class);
			
			RequestDecode.addRequest(cmdID, handler.request());
			ResponseDecode.addResponse(cmdID, handler.response());
		}
	}
}

package com.bear.demo.protobuff.tryself5;

import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import com.gzyouai.hummingbird.engine.tryself5.decode.RequestDecode;
import com.gzyouai.hummingbird.engine.tryself5.decode.ResponseDecode;
import com.gzyouai.hummingbird.engine.tryself5.handle.Handle;
import com.gzyouai.hummingbird.engine.tryself5.handle.HandleRegistered;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.WalkReq;
import com.gzyouai.hummingbird.engine.tryself5.initialization.LoadManager;
import com.gzyouai.hummingbird.engine.tryself5.util.NullUtil;

public class MessageSystemTest {

	@Before
	public void setUp() throws Exception {
		LoadManager.run();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() throws Exception {
		byte[] reqSrc = WalkReq.newBuilder()
				   .setStepLength(100)
				   .setStepNumber(5).build().toByteArray();

		int cmdID = 101;
		MessageLite message = RequestDecode.decodeRequest(cmdID, reqSrc);
		Builder builder = ResponseDecode.decodeResponse(cmdID);
		Handle handle = HandleRegistered.getHandle(cmdID);
		if(NullUtil.anyNull(handle,message,builder)){
			throw new NullPointerException(String.format("@@@ 业务逻辑处理失败! - cmdID: %d, message: %s, builder: %s, handle: %s", cmdID, message, builder, handle));
		}
		handle.exec(message, builder);
	}
}

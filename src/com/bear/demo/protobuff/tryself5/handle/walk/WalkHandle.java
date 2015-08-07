package com.bear.demo.protobuff.tryself5.handle.walk;

import com.gzyouai.hummingbird.engine.tryself5.handle.AbstractHandle;
import com.gzyouai.hummingbird.engine.tryself5.handle.HandlerRegister;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.WalkReq;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.WalkRsp;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.WalkRsp.Builder;

@HandlerRegister(request=WalkReq.class, response = WalkRsp.class)
public class WalkHandle extends AbstractHandle<WalkReq, WalkRsp.Builder>{

	@Override
	public void exec(WalkReq request, Builder rspBuilder) {
		System.out.println("WalkHandle doing...");
	}
}

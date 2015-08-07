package com.bear.demo.protobuff.tryself5.handle.quest;

import com.gzyouai.hummingbird.engine.tryself5.handle.AbstractHandle;
import com.gzyouai.hummingbird.engine.tryself5.handle.HandlerRegister;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.MyMsgReq;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.MyMsgRsp;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.MyMsgRsp.Builder;

@HandlerRegister(request=MyMsgReq.class, response = MyMsgRsp.class)
public class QuestHandle extends AbstractHandle<MyMsgReq, MyMsgRsp.Builder>{

	@Override
	public void exec(MyMsgReq request,Builder rspBuilder) {
		System.out.println("QuestHandle doing... ");
	}
}

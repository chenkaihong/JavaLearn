package com.bear.demo.protobuff;

import com.bear.demo.protobuff.Cmd.CmdData;
import com.bear.demo.protobuff.Cmd.CmdMessage;
import com.bear.demo.protobuff.Cmd.CmdTest;
import com.bear.scan.Demos;
import com.bear.scan.Description;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Description(description="ProtoBuf 编码与解码", sort="P")
public class ProtoBufDemo implements Demos{

	public static void main(String[] args) throws Exception {
		
		CmdData cmd = newData();
		ByteBuf buf = Unpooled.wrappedBuffer(cmd.toByteArray());
		CmdData model = CmdData.parseFrom(buf.array());
		
		CmdTest test = CmdTest.parseFrom(model.getData().toByteArray());
		
		System.out.println(buf);
		System.out.println(model.getMessage().getPlayerId());
		
		System.out.println(test.getId());
	}
	
	private static CmdData newData(){
		CmdData.Builder cdb = CmdData.newBuilder();

		CmdTest.Builder builder = CmdTest.newBuilder();
		builder.setId(33333);
		builder.setName("wuava");

		CmdMessage.Builder cmb = CmdMessage.newBuilder();
		cmb.setMessageId(1100);
		cmb.setClientTime(1);
		cmb.setPlayerId(1);
		cmb.setServerId(1);
		cmb.setServerTime(1);

		cdb.setMessage(cmb);
		cdb.setData(builder.build().toByteString());
		CmdData cmd = cdb.build();
		return cmd;
	}
}
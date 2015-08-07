package com.bear.demo.protobuff;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.ArrayList;
import java.util.List;

import com.bear.demo.protobuff.Cmd.CmdData;
import com.bear.demo.protobuff.Cmd.CmdMessage;
import com.bear.demo.protobuff.Cmd.CmdTest;
import com.bear.scan.Demos;
import com.bear.scan.Description;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

@Description(description="ProtoBuf 编码与解码", sort="P")
public class ProtoBufDemo implements Demos{

	public static void main(String[] args) throws Exception {
		
		Encode encode = new Encode();
		Decode decode = new Decode(Cmd.CmdData.getDefaultInstance());
		
		
		CmdData cmd = newData();
		
		List<ByteBuf> bufList = encode.encode(cmd);
		
		List<CmdData> model = decode.decode(bufList.get(0));
		
		System.out.println(bufList);
		
		System.out.println(model.get(0).getMessage().getPlayerId());
		
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

class Encode extends ProtobufEncoder{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ByteBuf> encode(MessageLiteOrBuilder msg) throws Exception{
		List list = new ArrayList();
		super.encode(null, msg, list);
		return list;
	}
}
class Decode extends ProtobufDecoder{
	public Decode(MessageLite prototype) {
		super(prototype);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> decode(ByteBuf msg) throws Exception{
		List<Object> list = new ArrayList<Object>();
		super.decode(null, msg, list);
		return (List<T>) list;
	}
}

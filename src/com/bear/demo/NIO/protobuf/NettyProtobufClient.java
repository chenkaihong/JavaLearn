package com.bear.demo.NIO.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.bear.demo.NIO.protobuf.CmdBox.Cmd;
import com.bear.demo.NIO.protobuf.CmdBox.Message;

public class NettyProtobufClient {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ClientConfig());
            Channel ch = b.connect("127.0.0.1", 8080).sync().channel();
            	
            Message message = Message.newBuilder().setMessageId(1200000).setServerId(12).build();
            Cmd cmd = Cmd.newBuilder().setMessage(message).build();
            ch.writeAndFlush(cmd);
            
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
	}
}

class ClientConfig extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(CmdBox.Cmd.getDefaultInstance()));

        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());

        p.addLast(new ClientHandler());
    }
}

class ClientHandler extends SimpleChannelInboundHandler<Cmd> {
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("Registered!");
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Cmd cmd) throws Exception {
        System.out.println("body: " + cmd);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

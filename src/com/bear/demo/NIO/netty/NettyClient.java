package com.bear.demo.NIO.netty;

import java.util.Scanner;

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
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ClientConfig());
            Channel ch = b.connect("127.0.0.1", 8080).sync().channel();
            Scanner read = new java.util.Scanner(System.in);
            while(true){
            	System.out.println("Please insert your word: ");
            	String body = read.next();
            	if(!body.endsWith("\n")){
            		body += "\n";
            	}
            	ch.writeAndFlush(body);
            	
            	if("out\n".equals(body)){
            		break;
            	}
            }
            
            read.close();
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

        p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        p.addLast(new StringDecoder());
        p.addLast(new StringEncoder());

        p.addLast(new ClientHandler());
    }
}

class ClientHandler extends SimpleChannelInboundHandler<String> {
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("Registered!");
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String body) throws Exception {
        System.out.println("body: " + body);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

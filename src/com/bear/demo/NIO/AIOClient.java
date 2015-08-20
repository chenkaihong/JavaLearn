package com.bear.demo.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class AIOClient {

	public static void main(String[] args) {
		
	}
	
	public void start() throws IOException{
		
		AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
		channel.bind(new InetSocketAddress(8080));
		
		
		
	}
}

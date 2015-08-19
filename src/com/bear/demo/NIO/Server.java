package com.bear.demo.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {

	public static void main(String[] args) throws IOException {
		
		Selector selector = Selector.open();
		
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress(8282));
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_ACCEPT);
		
	}
	
}

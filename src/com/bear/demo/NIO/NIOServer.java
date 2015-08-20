package com.bear.demo.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

	public static void main(String[] args) throws IOException {
		new NIOServer().start();
	}
	
	public void start() throws IOException{
		Selector selector = Selector.open();
		
		ServerSocketChannel channel = ServerSocketChannel.open();
		
		channel.configureBlocking(false);
		channel.socket().bind(new InetSocketAddress("127.0.0.1", 8080));
		channel.register(selector, SelectionKey.OP_ACCEPT);
		
		while(true){
			selector.select(1000);
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
			while(it.hasNext()){
				SelectionKey key = it.next();
				it.remove();
				
				try{
					handle(key, selector);
				}catch(Exception e){
					e.printStackTrace();
					cancel(key);
				}
			}
		}
	}
	
	public void handle(SelectionKey key, Selector selector) throws IOException{
		if(key.isValid()){
			if(key.isAcceptable()){
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			
			if(key.isReadable()){
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer inBuff = ByteBuffer.allocate(1024);
				int readBytes = sc.read(inBuff);
				if(readBytes > 0){
					inBuff.flip();
					byte[] inTemp = new byte[readBytes];
					inBuff.get(inTemp);
					String body = new String(inTemp, "UTF-8");
					System.out.println("body: " + body);
					
					byte[] outTemp = "Copy That!".getBytes();
					ByteBuffer outBuff = ByteBuffer.allocate(outTemp.length);
					outBuff.put(outTemp);
					outBuff.flip();
					sc.write(outBuff);
				} else if(readBytes < 0){
					key.cancel();
					sc.close();
				} else{
					// nothing to do!
				}
			}
		}
	}
	
	public static void printBuffInformation(ByteBuffer buff){
		System.out.println("Capacity: " + buff.capacity());
		System.out.println("Limit: " + buff.limit());
		System.out.println("Position: " + buff.position());
		System.out.println("Mark: " + buff.mark());
		System.out.println();
	}
	
	public static void cancel(SelectionKey key) throws IOException{
		if(key != null){
			key.cancel();
			if(key.channel() != null){
				key.channel().close();
			}
		}
	}
}

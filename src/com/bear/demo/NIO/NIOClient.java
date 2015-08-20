package com.bear.demo.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;


public class NIOClient {
	public static void main(String[] args) throws IOException {
		new NIOClient().start();
	}
	
	public void start() throws IOException{
		
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(true);
		sc.connect(new InetSocketAddress("127.0.0.1", 8080));
		if(sc.isConnected()){
			Scanner read = new Scanner(System.in);
			while(true){
				System.out.println("Please insert your word: ");
				String sendMsg = read.next();
				
				byte[] outTemp = sendMsg.getBytes();
				ByteBuffer outBuff = ByteBuffer.allocate(outTemp.length);
				outBuff.put(outTemp);
				outBuff.flip();
				sc.write(outBuff);
				
				ByteBuffer inBuff = ByteBuffer.allocate(1024);
				int readBytes = sc.read(inBuff);
				if(readBytes > 0){
					inBuff.flip();
					byte[] inTemp = new byte[inBuff.remaining()];
					inBuff.get(inTemp);
					String body = new String(inTemp, "UTF-8");
					System.out.println(body);
					
					if("out".equals(sendMsg)){
						break;
					}
				}else if(readBytes < 0){
					sc.close();
				}else{
					
				}
			}
			
			read.close();
			sc.close();
		}
	}
}


//if(sc.isConnected()){
//	sc.register(selector, SelectionKey.OP_READ);
//	byte[] temp = "Hi".getBytes();
//	ByteBuffer buff = ByteBuffer.allocate(temp.length);
//	buff.get(temp);
//	buff.flip();
//	sc.write(buff);
//}

//while(true){
//	selector.select(1000);
//	Set<SelectionKey> keys = selector.selectedKeys();
//	Iterator<SelectionKey> it = keys.iterator();
//	while(it.hasNext()){
//		SocketChannel sc = it.next();
//	}
//}

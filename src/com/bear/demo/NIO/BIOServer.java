package com.bear.demo.NIO;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="BIO 示例", sort="B")
public class BIOServer implements Demos{

	public static void main(String[] args) {
		
		int port = 8080;
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			
			while(true){
				Socket socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			close(server);
		}
	}
	
	public static void close(ServerSocket server){
		try {
			if(server != null)
				server.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			server = null;
		}
	}
	
	public static void close(Closeable obj){
		try {
			if(obj != null){
				obj.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			obj = null;
		}
	}
}

class TimeServerHandler implements Runnable{
	private Socket socket;
	public TimeServerHandler(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			while(true){
				String msg = in.readLine();
				if(msg == null)
					break;
				System.out.println(msg);
				out.println("Copy that!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			BIOServer.close(in);
			out.flush();
			BIOServer.close(out);
		}
	}
	
}
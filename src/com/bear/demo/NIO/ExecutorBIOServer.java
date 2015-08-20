package com.bear.demo.NIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorBIOServer {

	public static void main(String[] args) throws IOException {
		
		ServerSocket server = new ServerSocket(8080);
		ExecutorService pool = Executors.newFixedThreadPool(4);
		
		while(true){
			Socket socket = server.accept();
			pool.execute(new Handler(socket));
		}
	}
}

class Handler implements Runnable{
	private Socket socket;
	
	public Handler(Socket socket){
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

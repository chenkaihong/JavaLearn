package com.bear.demo.NIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ExecutorBIOClient {

	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			Socket socket = new Socket("127.0.0.1", 8080);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			while(true){
				System.out.println("Insert your order: ");
				Scanner read = new Scanner(System.in);
				String inRead = read.next();
				out.println(inRead);
				String msg = in.readLine();
				System.out.println(msg);
				if("out".equals(inRead))
					break;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			BIOServer.close(in);
			BIOServer.close(out);
		}
		
	}
}

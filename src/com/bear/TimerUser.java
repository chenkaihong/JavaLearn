package com.bear;

import java.util.Timer;
import java.util.TimerTask;

import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="定时器的使用", sort="T")
public class TimerUser implements Demos{

	public static void main(String[] args) throws InterruptedException {
		Timer timer = new Timer("Test", true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("1");
			}
		};
		timer.schedule(task, 1000L, 1000L);
		
		Thread.sleep(1000000000L);
	}
	
}

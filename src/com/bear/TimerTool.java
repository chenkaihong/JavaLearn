package com.bear;

import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="使用守护现成检测当前时间变动,从而做出变化", sort="T")
public abstract class TimerTool implements Demos{
	
	private long nowTime;
	private long beforeTime;
	private final long period;
	private final long upperLimit;
	private final long lowerLimit;

	public static void main(String[] args) throws InterruptedException {
		
		new TimerTool(1000L, 500L) {
			@Override
			public void operation(long beforeTime, long nowTime, long minus) {
				System.out.println("Reload! - " + minus);
			}
		}.start();
		
		Thread.sleep(10000000L);
	}
	
	public TimerTool(long period, long errorRange){
		this.period = period;
		this.upperLimit = period+errorRange;
		this.lowerLimit = period-errorRange;
	}
	
	public void start(){
		nowTime = System.currentTimeMillis();
		beforeTime = nowTime - period;
		
		Thread timeDaemonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					nowTime = System.currentTimeMillis();
					long minus = Math.abs(nowTime - beforeTime);
					if(minus < lowerLimit || minus > upperLimit){
						operation(beforeTime, nowTime, minus);
					}
					beforeTime = nowTime;
					try {
						Thread.sleep(period);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		timeDaemonThread.setDaemon(true);
		timeDaemonThread.start();
	}
	
	public abstract void operation(long beforeTime, long nowTime, long minus);
	
}

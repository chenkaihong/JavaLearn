package com.gzyouai.hummingbird.engine.tryself5.initialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class LoadManager {
	
	private static Map<String,Loader> loaderMap;
	private static Logger log = Logger.getLogger(LoadManager.class);
	
	static{
		loaderMap = new HashMap<String,Loader>();
		
		loaderMap.put("静态业务处理逻辑", new HandleLoader());
		loaderMap.put("Request And Response", new RequestAndResponseLoader());
	}
	
	public static void run(){
		String des = "";
		log.info("=================  开始加载游戏内容  =======================================================");
		try{
			for(Entry<String, Loader> entry : loaderMap.entrySet()){
				des = entry.getKey();
				Loader loader = entry.getValue();
				long beginTime = System.currentTimeMillis();
				log.info(String.format("开始加载 %s", des));
				loader.load();
				long endTime = System.currentTimeMillis();
				long useTime = endTime-beginTime;
				log.info(String.format("%s 加载完毕 - 耗时%dms", des, useTime));
				if(useTime > 60*1000){
					log.error(String.format("%s 任务耗时超过1分钟, 或许可以优化一下", des));
				}
			}
		}catch(Exception e){
			log.error("@@@ 加载游戏内容失败 -> \n", e);
			System.exit(1);
		}
		log.info("=================  结束加载游戏内容  =======================================================\n");
	}
}


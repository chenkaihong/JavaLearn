package com.bear.tool;

import java.io.Closeable;
import java.io.IOException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by ckh
 * @Date 2015年3月13日 下午6:24:12
 * @Description 
 */
public class ToolClose {
	/**
	 * 关闭资源对象
	 * @param resource
	 */
	public static void close(Closeable ...resource){
		if(resource != null && resource.length > 0){
			for(Closeable s : resource){
				if(s != null){
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally{
						s = null;
					}
				}
			}
		}
	}
}

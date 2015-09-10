package com.bear.demo.ehcache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class TestGetCache {
	
	public static void main(String[] args) throws Exception {
		testGet();
	}
	
	public static void testGet() throws Exception {
		URL url = TestGetCache.class.getClassLoader().getResource("ehcache1.xml");
		CacheManager manager = new CacheManager(url);
		
		Cache cache = manager.getCache("metaCache");
		
		while (true) {
			System.out.println("搜索中...");
			System.out.println("当前资源数：" + cache.getSize());
			Element element = cache.get("key");
			if (element != null) {
				User user = (User)element.getObjectValue();
				System.out.println(user.getName());
				break;
			}
			Thread.sleep(1000);
		}
	}
}

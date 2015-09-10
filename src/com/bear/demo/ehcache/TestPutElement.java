package com.bear.demo.ehcache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class TestPutElement{
	
	public static void main(String[] args) throws Exception {
		testPut();
	}
	
	public static void testPut() throws Exception {
		URL url = TestPutElement.class.getClassLoader().getResource("ehcache2.xml");
		CacheManager manager = new CacheManager(url);
		
		Cache cache = manager.getCache("metaCache");

		User user = new User();
		user.setName("张三");
		Element element = new Element("key",user);
		cache.put(element);

		manager.shutdown();
		System.out.println("已放入缓存！");
	}
}
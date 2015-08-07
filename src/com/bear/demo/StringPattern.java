package com.bear.demo;

import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="replaceAll的转义字符的使用", sort="S")
public class StringPattern implements Demos{

	public static void main(String[] args) {
		String temp = "d/f/e/f";
		
		// 这个地方的\\\\ 先是编译上的转化->\\, 然后replaceAll在调用正则表达式, 则\\->\
		System.out.println(temp.replaceAll("/", "\\\\"));
	}
	
}

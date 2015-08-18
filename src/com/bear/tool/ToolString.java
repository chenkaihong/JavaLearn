package com.bear.tool;

import java.io.UnsupportedEncodingException;

public class ToolString {
	
	public static boolean isEmpty(String temp){
		if(temp == null){
			return true;
		}
		temp = temp.trim();
		return temp.length() <= 0;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		// xF0 x9F x92 x95
		// xf0 x9f x92 x80
		// xF0 x9F x8C xB9
		// 💀 UTF-8 4字节模式
		
//		 byte[] bytes = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x8C, (byte) 0xB9};
//		 System.out.println(new String(bytes,"UTF-8"));
		
//		System.out.println(hasUTF_4("abdki汉字💀VI、%$^%&"));     // 这行中,💀是UTF-8的4字节模式
//		System.out.println(hasUTF_4("#$%@#$%fdsf12312贰＆ァБ"));
		
		System.out.println(hasUTF_4("💕"));
		
		System.out.println(hasUTF_4("🌹"));
		
		for(byte s : "🌹".getBytes()){
			System.out.println(Integer.toHexString(s));
		}
	}
	
	/**
	 * 判断一个String中是否存在UTF-8的四字节模式
	 * @param temp Java内存中所有的字符都是以Unicode编码存在
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean hasUTF_4(String temp) throws UnsupportedEncodingException{
		byte[] bytes = temp.getBytes("UTF-8");
		return hasUTF_4(bytes);
	}
	
	/**
	 * 判断一个byte[]中是否存在UTF-8的四字节模式(因为UTF-8是变长编码,所以需要通过判断模式来确定一个语义到底包含多少个byte)
	 * @param bytes
	 * @return
	 */
	public static boolean hasUTF_4(byte[] bytes){
		// 用于跳过已经判断出模式的字节
		int index = 0;
		while(index < bytes.length){
			if(isUTF_1(bytes[index])){
				index += 1;
				continue;
			}
			
			if(isUTF_2(bytes[index])){
				index += 2;
				continue;
			}
			
			if(isUTF_3(bytes[index])){
				index += 3;
				continue;
			}
			
			if(isUTF_4(bytes[index])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否UTF-8的1字节模式
	 * @param b
	 * @return
	 */
	public static boolean isUTF_1(byte b){
		// UTF-8 4字节模式为: 0xxxxxxx
		// b传入时很可能是int,其位数为32位,所以使用0xFF进行低8位过滤(byte: 8位)
		// 0x80对b的低7位进行过滤
		return ((0xFF & b) & 0x80) == 0x00;
	}
	/**
	 * 判断是否UTF-8的2字节模式
	 * @param b
	 * @return
	 */
	public static boolean isUTF_2(byte b){
		// UTF-8 4字节模式为: 110xxxxx
		// b传入时很可能是int,其位数为32位,所以使用0xFF进行低8位过滤(byte: 8位)
		// 0xE0对b的低5位进行过滤
		return ((0xFF & b) & 0xE0) == 0xC0;
	}
	/**
	 * 判断是否UTF-8的3字节模式
	 * @param b
	 * @return
	 */
	public static boolean isUTF_3(byte b){
		// UTF-8 4字节模式为: 1110xxxx
		// b传入时很可能是int,其位数为32位,所以使用0xFF进行低8位过滤(byte: 8位)
		// 0xF0对b的低4位进行过滤
		return ((0xFF & b) & 0xF0) == 0xE0;
	}
	/**
	 * 判断是否UTF-8的4字节模式
	 * @param b
	 * @return
	 */
	public static boolean isUTF_4(byte b){
		// UTF-8 4字节模式为: 11110xxx
		// b传入时很可能是int,其位数为32位,所以使用0xFF进行低8位过滤(byte: 8位)
		// 0xF8对b的低3位进行过滤
		return ((0xFF & b) & 0xF8) == 0xF0;
	}
	
}

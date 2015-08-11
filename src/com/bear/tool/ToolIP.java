package com.bear.tool;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by bear_ckh
 * @Date 2014年9月10日 下午2:43:15
 * @Description 
 */
public class ToolIP {
	public static boolean equalsIP(String outIP) {
		Enumeration<NetworkInterface> allNetInterfaces;  //定义网络接口枚举类  
        try {  
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();  //获得网络接口  
  
            InetAddress ip = null; //声明一个InetAddress类型ip地址  
            while (allNetInterfaces.hasMoreElements()) //遍历所有的网络接口  
            {  
                NetworkInterface netInterface = allNetInterfaces.nextElement();  
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); //同样再定义网络地址枚举类  
                while (addresses.hasMoreElements())  
                {  
                    ip = addresses.nextElement();  
                    if (ip != null && (ip instanceof Inet4Address)) //InetAddress类包括Inet4Address和Inet6Address  
                    {  
                        String realIP = ip.getHostAddress();
                        if(outIP.equals(realIP)){
                        	return true;
                        }
                    }   
                }  
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        }
        return false;
	}
}

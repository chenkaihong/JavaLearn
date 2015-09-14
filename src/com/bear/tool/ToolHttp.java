package com.bear.tool;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP请求相关
 * @author 董华健
 */
public class ToolHttp {

//	private static Logger log = Logger.getLogger(ToolHttp.class);
	
	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, CharSequence type) {
    	String result = null;
        try {
            String urlNameString = url;
            if(!ToolString.isEmpty(param)){
            	urlNameString += "?" + param;
            }
            
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            
//            Map<String, List<String>> map = connection.getHeaderFields();
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            
            result = ToolString.fromStreamToString(connection.getInputStream(), type);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        return result;
    }
    public static String sendGet(String url, Map<String, String> paramMap, CharSequence type) {
    	StringBuilder sb = new StringBuilder();
    	if(paramMap != null && paramMap.size() > 0){
	    	for(Entry<String, String> e : paramMap.entrySet()){
	    		sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
	    	}
	    	sb.deleteCharAt(sb.length()-1);
    	}
    	return sendGet(url, sb.toString(), type);
    }
    
    public static void main(String[] args) {
		System.out.println(ToolHttp.sendGet("http://www.baidu.com", "", ToolString.encoding));
	}
}


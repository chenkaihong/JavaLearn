package com.bear.demo.LBS;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.bear.tool.ToolFile;
import com.bear.tool.ToolHttp;
import com.bear.tool.ToolString;
import com.google.gson.Gson;

/*
 * 百度地图API
 */
public final class BaiDuMapApi {
 
    public static void main(String[] args) throws IOException {
    	BaiDuMapApi.getPoint();
//    	
//    	String jsonString = ToolFile.fromFileToString("D:\\1.txt");
//    	
//    	Gson json = new Gson();
//        LBS_Local_Model local = json.fromJson(jsonString, LBS_Local_Model.class);
//        System.out.println(json.toJson(local));
	}
 
    /*
     * 根据ip获取经纬度
     */
    public static Map<String, String> getPoint() {
    	Map<String, String> params = new HashMap<String, String>();
//        params.put("ip", "127.0.0.1");
        params.put("ak", BaiDuApi.BD_LBS_AK);
        params.put("coor", "bd09ll");
        
        String localModel = ToolHttp.sendGet(BaiDuApi.BD_IP_LOCATION_API, params, ToolString.encoding);
        
        System.out.println(localModel);
        
        // 删除json前的编码字段
        
        
        Gson json = new Gson();
        LBS_Local_Model local = json.fromJson(localModel, LBS_Local_Model.class);
        System.out.println(json.toJson(local));
//        String json = StringUtils.unicodeToString(r);
//        Map<String, Object> map = JSONUtil.json2Map(json);
//        Map<String, Object> content = JSONUtil.json2Map(map.get("content").toString());
//        Map<String, String> xy = JSONUtil.json2Map(content.get("point").toString());
        
//        Map<String, String> point = new HashMap<String, String>();
//        point.put("latitude", xy.get("y"));
//        point.put("longitude", xy.get("x"));
        return null;
    }
 
//    /*
//     * 根据城市和地址获取经纬度
//     */
//    public static Map<String, String> getPoint(String city, String address) {
//        params.clear();
//        Map<String, String> point = CollectionUtil.newHashMap();
//        if (StringUtils.isNotBlank(city) && StringUtils.isNotBlank(address)) {
//            params.put("ak", BaiDuApi.BD_LBS_AK);
//            params.put("callback", "renderOption");
//            params.put("output", "json");
//            params.put("city", city);
//            params.put("address", address);
//            String r = HttpKit.get(BaiDuApi.BD_LOCATION2POINT_API, params);
//            String json = StringUtils.unicodeToString(r.substring(r.indexOf("{"), r.lastIndexOf("}") + 1));
//            Map<String, Object> map = JSONUtil.json2Map(json);
//            Map<String, Object> content = JSONUtil.json2Map(map.get("result").toString());
//            Map<String, Object> location = JSONUtil.json2Map(content.get("location").toString());
//            point.put("latitude", location.get("lat").toString());
//            point.put("longitude", location.get("lng").toString());
//        }
//        return point;
//    }
// 
//    /*
//     * 根据ip获取address
//     */
//    public static Map<String, String> getAddress(String ip) {
//        params.clear();
//        Map<String, String> address_detail = CollectionUtil.newHashMap();
//        if (null != ip && !LOCAL_IP.contains(ip)) {
//            params.put("ip", ip);
//        }
//        params.put("ak", BaiDuApi.BD_LBS_AK);
//        String r = HttpKit.get(BaiDuApi.BD_IP_LOCATION_API, params);
//        String json = StringUtils.unicodeToString(r);
//        Map<String, Object> map = JSONUtil.json2Map(json);
//        Map<String, Object> content = JSONUtil.json2Map(map.get("content").toString());
//        address_detail = JSONUtil.json2Map(content.get("address_detail").toString());
//        return address_detail;
//    }
// 
//    /*
//     * 根据经纬度获取详细地址
//     */
//    public static Map<String, Object> getAddress(String latitude, String longitude) {
//        params.clear();
//        Map<String, Object> info = CollectionUtil.newHashMap();
//        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
//            params.put("callback", "renderReverse");
//            params.put("ak", BaiDuApi.BD_LBS_AK);
//            params.put("location", latitude + "," + longitude);
//            params.put("output", "json");
//            params.put("pois", "0");
//            String r = HttpKit.get(BaiDuApi.BD_LOCATION2POINT_API, params);
//            String json = StringUtils.unicodeToString(r.substring(r.indexOf("{"), r.lastIndexOf("}") + 1));
//            Map<String, Object> map = JSONUtil.json2Map(json);
//            info = JSONUtil.json2Map(map.get("result").toString());
//        }
//        return info;
//    }
 
}
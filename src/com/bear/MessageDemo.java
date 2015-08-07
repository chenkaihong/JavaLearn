package com.bear;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bear.scan.Description;
import com.bear.tool.ToolClassFind;
import com.bear.tool.ToolPath;
import com.google.gson.Gson;

@Description(description="消息分配器和数据填充工厂, 通过注解解决消息分配问题", sort="M")
public class MessageDemo {
	
	public static void main(String[] args) throws Exception {
		
		List<Class<?>> classes = ToolClassFind.of(GameSystem.class, ToolPath.getRootClassPath()).search();
		for(Class<?> clazz : classes){
			for(Method method : clazz.getMethods()){
				MessageMothed messageMothed = method.getAnnotation(MessageMothed.class);
				if(messageMothed != null){
					MessageProcess process = new MessageProcess(PlayerSystem.class, method);
					MessageIndex.me().addMessageProcess(messageMothed.messageID(), process);
				}
			}
		}
		
		DataFillFactory fillFactory = DataFillFactory.newOf(JsonFill.newOf());
		String json = "{\"playerID\":1200000,\"serverID\":12,\"contentMap\":{\"3\":\"3\",\"2\":\"2\",\"1\":\"1\"}}";
		
		System.out.println(MessageIndex.me().process(20001, json, fillFactory));
	}
	
}

class MessageProcess{
	public final Class<?> systemClass;
	public final Method method;
	public final Object processObj;
	
	public MessageProcess(Class<?> systemClass, Method method) throws Exception {
		this.systemClass = systemClass;
		this.method = method;
		this.processObj = systemClass.newInstance();
	}
}

/**
 * 分配器核心类
 * @author Administrator
 *
 */
class MessageIndex{
	
	private Map<Integer, MessageProcess> processMap = new HashMap<Integer, MessageProcess>();
	private final static MessageIndex me = new MessageIndex();
	
	private MessageIndex(){
		
	}
	public static MessageIndex me(){
		return me;
	}
	
	/**
	 * 增加处理方案
	 * @param messageID
	 * @param process
	 */
	public void addMessageProcess(int messageID, MessageProcess process){
		if(processMap.containsKey(messageID)){
			throw new RuntimeException("Have same messageID: " + messageID);
		}
		processMap.put(messageID, process);
	}
	/**
	 * 进行任务分配
	 * @param messageID
	 * @param contentJson
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object process(int messageID, String contentJson, DataFillFactory fillFactory) throws Exception{
		MessageProcess process = processMap.get(messageID);
		Class<?>[] parmClasses = process.method.getParameterTypes();
		
		if(parmClasses == null || parmClasses.length != 1){
			throw new RuntimeException(String.format("@@@ Parameter's number is error, the number must be one! - ClassName: %s, MethodName: %s", 
																																		process.systemClass.getName(), 
																																		process.method.getName()));
		}
		
		Object request = fillFactory.inFill(contentJson, parmClasses[0]);
		Object temp = process.method.invoke(process.processObj, request);
		Object response = fillFactory.outFill(temp);
		return response;
	}
}

/**
 * 数据填充工厂, 
 * @author Administrator
 *
 */
class DataFillFactory{
	private final DataFill tool;
	private DataFillFactory(DataFill tool){
		this.tool = tool;
	}
	
	public static DataFillFactory newOf(DataFill tool){
		return new DataFillFactory(tool);
	}
	
	public Object inFill(String json, Class<?> type){
		return tool.inFill(json, type);
	}
	public Object outFill(Object content){
		return tool.outFill(content);
	}
}
interface DataFill{
	Object inFill(String json, Class<?> type);
	Object outFill(Object content);
	
}
class JsonFill implements DataFill{
	private JsonFill(){}
	public static JsonFill newOf(){
		return new JsonFill();
	}
	
	@Override
	public Object inFill(String json, Class<?> type) {
		Object request = new Gson().fromJson(json, type);
		return request;
	}
	@Override
	public Object outFill(Object content) {
		return new Gson().toJson(content);
	}
}

/**
 * 游戏系统标记接口
 * @author Administrator
 *
 */
interface GameSystem{
	
}
@MessageSystem(startID=20000, finishID=20199, description="JUnitSystem")
class PlayerSystem implements GameSystem{
	
	@MessageMothed(messageID=20001, description="JUnit")
	public Response function2(Request request){
		request.getPlayerID();
		
		Response response = new Response();
		response.setPlayerID(120000);
		response.setServerID(12);
		return response;
	}
}

class Response{
	private int playerID;
	private int serverID;
	private Map<String, String> contentMap = new HashMap<String, String>();
	private Map<Integer, String> errorReturn = new HashMap<Integer, String>();
	
	public void setParm(String name, String content){
		contentMap.put(name, content);
	}
	public void setError(int errorID, String errorMsg){
		errorReturn.put(errorID, errorMsg);
	}
	
	public int getPlayerID() {
		return playerID;
	}
	public int getServerID() {
		return serverID;
	}
	public Map<String, String> getContentMap() {
		return contentMap;
	}
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	public void setContentMap(Map<String, String> contentMap) {
		this.contentMap = contentMap;
	}
	public Map<Integer, String> getErrorReturn() {
		return errorReturn;
	}
	public void setErrorReturn(Map<Integer, String> errorReturn) {
		this.errorReturn = errorReturn;
	}
}
class Request{
	private int playerID;
	private int serverID;
	private Map<String, String> contentMap;
	
	public String getParm(String name){
		return contentMap.get(name);
	}
	
	public int getPlayerID() {
		return playerID;
	}
	public int getServerID() {
		return serverID;
	}
	public Map<String, String> getContentMap() {
		return contentMap;
	}
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	public void setContentMap(Map<String, String> contentMap) {
		this.contentMap = contentMap;
	}
}

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@interface MessageMothed {
	int messageID();
	String description();
}

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@interface MessageSystem {
	int startID();
	int finishID();
	String description();
}
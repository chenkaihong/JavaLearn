package com.bear.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ToolProperties {
	private final Properties properties;
	public ToolProperties(String path) throws FileNotFoundException, IOException{
		properties = new Properties();
		properties.load(new FileReader(new File(path)));
	}
	
	public Properties getResource(){
		return properties;
	}
	
	public String getProperty(String key){
		String result = properties.getProperty(key);
		if(result == null){
			throw new RuntimeException("properties no find the key! key : " + key);
		}
		return  result;
	}
}

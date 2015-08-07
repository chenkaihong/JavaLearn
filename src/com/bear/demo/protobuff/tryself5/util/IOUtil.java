package com.gzyouai.hummingbird.engine.tryself5.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOUtil {

	public static File writeObjectToFile(String fileString, Object obj) throws FileNotFoundException, IOException{
		File file = new File(fileString);
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
			oos.flush();
		} finally{
			closeResource(oos);
		}
		return file;
	}
	
	public static Object readFileToObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(new FileInputStream(file));
			return ois.readObject();
		}finally{
			closeResource(ois);
		}
	}
	public static Object readFileToObject(String fileString) throws FileNotFoundException, ClassNotFoundException, IOException{
		return readFileToObject(new File(fileString));
	}
	
	public static void closeResource(Closeable resource){
		if(resource != null){
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				resource = null;
			}
		}
	}
}

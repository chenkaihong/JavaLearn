package com.bear.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class ToolFile {
	
	/**
	 * 迭代删除文件
	 * @param file
	 */
	public static void delete(File file) {
		if (file != null && file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
			else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i=0; i<files.length; i++) {
					delete(files[i]);
				}
			}
			file.delete();
		}
	}
	
	/**
	 * 将String写入文件
	 * @param content		需要写入文件的内容
	 * @param fileLocal		需要保存的文件路径
	 * @param isCover		是否需要覆盖原文件,如果不需要覆盖则继续在文件后方添加
	 * @param isMakeDir		是否创建父路径
	 * @return
	 * @throws IOException
	 */
	public static File fromStringToFile(String content, String fileLocal, boolean isCover, boolean isMakeDir) throws IOException{
		File file = new File(fileLocal);
		saveCheck(file, isMakeDir);
		
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		FileWriter fw = new FileWriter(file, !isCover);
		BufferedWriter bw = new BufferedWriter(fw);
		
		try{
			String temp;
			while((temp = br.readLine()) != null){
				bw.write(temp+"\n");
			}
			bw.flush();
		} finally{
			ToolClose.close(br, sr, bw, fw);
		}
		return file;
	}
	
	/**
	 * 在文件存档之前检查文件的路径是否可以访问,若不可以访问则抛出异常,若选择自动创建路径,则将isMakeDir置为true
	 * @param file
	 * @param isMakeDir true: 如果路径不合法,则创建父路径 - false: 直接抛出异常
	 * @param isCover 是否覆盖源文件
	 * @param isMakeDir 如果路径不合法,是否创建父路径
	 * @throws FileNotFoundException
	 */
	public static void saveCheck(File file, boolean isMakeDir) throws FileNotFoundException{
		if(file != null){
			if(file.isDirectory()){
				throw new FileNotFoundException("This path is a directory!You can't operate it! - " + file);
			}
			if(!file.exists()){
				File parentFile = file.getParentFile();
				if(!isExists(parentFile)){
					if(!isMakeDir){
						throw new FileNotFoundException("Can't find the parent's path! - " + file);
					}else{
						if(!parentFile.mkdirs()){
							throw new FileNotFoundException("Parent path create err! - " + file);
						}
					}
				}
			}
		}else{
			throw new FileNotFoundException("Can't process by a null file!");
		}
	}
	
	public static boolean isExists(File file){
		return file != null && file.exists();
	}
}

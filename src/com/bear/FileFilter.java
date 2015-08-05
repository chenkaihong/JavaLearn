package com.bear;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import com.bear.scan.Constant;
import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="文件过滤", sort=Constant.F)
public class FileFilter implements Demos{

	public static void main(String[] args) {
		File files = new File("D:/");
		
		String[] filesName = files.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		
		System.out.println(Arrays.toString(filesName));
	}
	
}

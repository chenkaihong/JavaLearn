package com.bear;

import java.io.File;

import com.bear.scan.Constant;
import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description = "跨平台使用分隔符", sort=Constant.F)
public class FileseparatorTest implements Demos{

	public static void main(String[] args) {
		System.out.println(File.separator);
		System.out.println(File.separatorChar);
		System.out.println(File.pathSeparator);
		System.out.println(File.pathSeparatorChar);
	}
	
}

// windows
// \
// \
// ;
// ;

// linux
// /
// /
// :
// :

package com.bear;

import com.bear.scan.Constant;
import com.bear.scan.Demos;
import com.bear.scan.Description;

@Description(description="判断一个类是否另一个类的子类", sort=Constant.C)
public class ClassInstanceof implements Demos{

	public static void main(String[] args) {
		
		// 判断类是否另一个类的子类
		System.out.println(A.class.isAssignableFrom(B.class)?"true":"false");
		
		B b = new B();
		
		System.out.println(b instanceof A);
		
	}
	
}

class A{}
class B extends A{}

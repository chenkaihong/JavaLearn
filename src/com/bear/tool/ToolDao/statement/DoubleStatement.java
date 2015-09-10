package com.bear.tool.ToolDao.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by wym  
 * @Date 2013-5-17 下午4:54:15
 * @Description 
 */
public class DoubleStatement implements ObjStatement{
	public Class<?> getType(){
		return Double.class;
	}
	
	public void set(int index, PreparedStatement preparedStatement,Object obj)  throws SQLException{
		preparedStatement.setDouble(index,(Double)obj);
	}

}

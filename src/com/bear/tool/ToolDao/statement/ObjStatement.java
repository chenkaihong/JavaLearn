package com.bear.tool.ToolDao.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by wym  
 * @Date 2013-5-17 下午4:52:31
 * @Description 
 */
public interface ObjStatement {
	public Class<?> getType();
	
	public void set(int index,PreparedStatement preparedStatement,Object obj) throws SQLException;
}

package com.bear.tool.ToolDao;
import java.sql.Connection;



/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by ckh
 * @Date 2012-7-24 下午8:27:13
 * @Description 
 */
public class DaoBase {

	protected final Connection getConnection()
	{
		return ConnectionManager.getGameDbConnection();
	}
	
}

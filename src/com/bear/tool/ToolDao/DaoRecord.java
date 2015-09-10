package com.bear.tool.ToolDao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by ckh
 * @Date 2015年8月31日 上午10:38:53
 * @Description 
 */
public interface DaoRecord {
	/**
	 * 解析一条数据库记录
	 * @param rs
	 * @throws SQLException
	 */
	public void parseResultSet (ResultSet rs) throws SQLException;
}

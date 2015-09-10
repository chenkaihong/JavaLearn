package com.bear.tool.ToolDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bear.tool.ToolPath;
import com.bear.tool.ToolProperties;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by ckh 
 * @Date 2012-7-23 下午4:52:57
 * @Description 
 */
public final class ConnectionManager {

	private static final Logger log=Logger.getLogger(ConnectionManager.class.getName());
	private static DataSource source;
	
	static{
		try {
			source = DruidDataSourceFactory.createDataSource(new ToolProperties(ToolPath.getRootClassPath() + "\\db.properties").getResource());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getGameDbConnection()
	{
		Connection conn = null;
		try {
			conn = source.getConnection();
			if (conn != null && !conn.getAutoCommit()) {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error("从gamedb连接池获取连接失败!",e);
			return null;
		}
		return conn;
	}
	
	public static void main(String[] args) throws Exception {
		
		List<Player> players = new CommonDao().batchSelectCallableStatement(Player.class, "select id,name from player");
		
		for(Player player : players){
			System.out.println("id: " + player.getId() + ", name: " + player.getName());
		}
	}
}

class Player implements DaoRecord{
	
	private int id;
	private String name;

	@Override
	public void parseResultSet(ResultSet rs) throws SQLException {
		this.id = rs.getInt("id");
		this.name = rs.getString("name");
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
}
package com.bear.tool.ToolDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.bear.tool.ToolDao.statement.BooleanStatement;
import com.bear.tool.ToolDao.statement.ByteStatement;
import com.bear.tool.ToolDao.statement.BytesStatement;
import com.bear.tool.ToolDao.statement.DateStatement;
import com.bear.tool.ToolDao.statement.DoubleStatement;
import com.bear.tool.ToolDao.statement.FloatStatement;
import com.bear.tool.ToolDao.statement.IntegerStatement;
import com.bear.tool.ToolDao.statement.LongStatement;
import com.bear.tool.ToolDao.statement.ObjStatement;
import com.bear.tool.ToolDao.statement.ShortStatement;
import com.bear.tool.ToolDao.statement.StringStatement;
import com.bear.tool.ToolDao.statement.TimestampStatement;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by ckh
 * @Date 2013-5-17 下午4:37:56
 * @Description
 */
public class CommonDao extends DaoBase {
	protected static final Logger Log = Logger.getLogger(CommonDao.class);

	public final static HashMap<Class<?>, ObjStatement> map = new HashMap<Class<?>, ObjStatement>();

	static {
		set(new BooleanStatement());
		set(new BytesStatement());
		set(new ByteStatement());
		set(new DateStatement());
		set(new DoubleStatement());
		set(new FloatStatement());
		set(new IntegerStatement());
		set(new LongStatement());
		set(new ShortStatement());
		set(new StringStatement());
		set(new TimestampStatement());
	}

	private final static void set(ObjStatement objStatement) {
		map.put(objStatement.getType(), objStatement);
	}

	/**
	 * 批量调用 CallableStatement
	 * 
	 * @param sql
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public boolean batchUpdateCallableStatement(String sql, List<List<Object>> data) throws Exception {		
		if (data.isEmpty()) {
			return true;
		}
		Connection conn = getConnection();
		CallableStatement pCall = null;
		try {
			conn.setAutoCommit(false);
			pCall = conn.prepareCall(sql);

			for (List<Object> row : data) {
				int index = 1;
				ObjStatement objStmt;
				for (Object val : row) {
					objStmt = map.get(val.getClass());
					if (objStmt != null) {
						objStmt.set(index++, pCall, val);
					}
					else {
						String msg = "未知类型[" + val.getClass() + "]";
						throw new Exception(msg);
					}
				}
				pCall.addBatch();
			}
			pCall.executeBatch();
			conn.commit();
			
			return true;
		}catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
			closePreparedStatement(pCall);
			closeConnection(conn);
		}
	}
	
	protected final String singleSelect(String sql,Object ...objData){
		String result = null;
		Connection connection=null;
		PreparedStatement ps=null;
		ResultSet rs = null;
		
		connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			if(objData.length > 0){
				for(int i = 0;i < objData.length;i++){
					ps.setObject(i+1, objData[i]);
				}
			}
			rs = ps.executeQuery();
			rs.next();
			result = rs.getString(1);
		} catch (SQLException e) {
			Log.error(0,e);//;e.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(ps != null){
					ps.close();
				}
				if(connection != null){
					connection.close();
				}
			} catch (SQLException e) {
				Log.error(0,e);//;e.printStackTrace();
			}finally{
				rs = null;
				ps = null;
				connection = null;
			}
		}
		
		return result;
	}
	
	/**
	 * 批量调用 CallableStatement(此批量执行没有使用注入,请使用的时候注意保证参数的安全性)
	 * 
	 * @param sql
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public boolean batchUpdateStatement(List<String> sqlList) throws Exception {		
		Connection conn = getConnection();
		Statement statement = null;
		try {
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			for(int i = 0;i < sqlList.size();i++){
				statement.addBatch(sqlList.get(i));
			}
			statement.executeBatch();
			conn.commit();
			return true;
		}catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
			closePreparedStatement(statement);
			closeConnection(conn);
		}
	}

	/**
	 * 更新 PreparedStatement
	 * @param sql
	 * @param vals
	 * @return
	 * 		是否更新成功(影响的记录行数是否大于0)
	 * @throws Exception
	 */
	public boolean updatePrepareStatement(String sql, Object... vals) throws Exception {		
		Connection conn = getConnection();
		PreparedStatement preparedStatement = null;
		boolean flag = false;
		try {
			preparedStatement = conn.prepareStatement(sql);
			int index = 1;
			ObjStatement objStatement;
			for (Object val : vals) {
				objStatement = map.get(val.getClass());
				if (objStatement != null) {
					objStatement.set(index++, preparedStatement, val);
				}
				else {
					String msg = "未知类型[" + val.getClass() + "]";
					throw new Exception(msg);
				}
			}
			int count = preparedStatement.executeUpdate();
			flag = count > 0;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closePreparedStatement(preparedStatement);
			closeConnection(conn);
		}
		return flag;
	}

	/**
	 * 执行带返回参数 CallableStatement
	 * @param sql
	 * @param vals
	 * @return
	 * 		返回的 CallableStatement 的 OUT 参数
	 * @throws Exception
	 */
	public int insertOutParameterStatement(String sql, Object... vals) throws Exception {		
		Connection conn = getConnection();
		CallableStatement callableStatement = null;
		int id = 0;
		ResultSet resSet = null;
		try {
			callableStatement = conn.prepareCall(sql);
			callableStatement.registerOutParameter(1, Types.INTEGER);
			int index = 2;
			ObjStatement objStatement;
			for (Object val : vals) {
				objStatement = map.get(val.getClass());
				if (objStatement != null) {
					objStatement.set(index++, callableStatement, val);
				}
				else {
					String msg = "未知类型[" + val.getClass() + "]";
					throw new Exception(msg);
				}
			}
			resSet = callableStatement.executeQuery();
			id = callableStatement.getInt(1);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(resSet, callableStatement, conn);
		}
		return id;
	}

	/**
	 * 批量插入并获取自增字段的值
	 * @param sql
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public List<Integer> batchInsertAndFecthAutoIdPrepareStatement (String sql, List<List<Object>> data) throws Exception {
		if (data.isEmpty()) {
			return new ArrayList<Integer>(0);
		}
		
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			for (List<Object> row : data) {
				int index = 1;
				ObjStatement objStmt;
				for (Object val : row) {
					objStmt = map.get(val.getClass());
					if (objStmt != null) {
						objStmt.set(index++, stmt, val);
					}
					else {
						String msg = "未知类型[" + val.getClass() + "]";
						throw new Exception(msg);
					}
				}
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
			
			List<Integer> newAutoIdList = new ArrayList<Integer>(data.size());
			rs = stmt.getGeneratedKeys();
			while (rs.next()) {
				newAutoIdList.add( rs.getInt(1) );
			}
			
			conn.setAutoCommit(true);
			return newAutoIdList;
		}
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		finally {
			close(rs, stmt, conn);
		}
	}
	
	/**
	 * 执行插入 PreparedStatement & CallableStatement, 并返回新增Id
	 * @param sql
	 * @param vals
	 * @return
	 * 		新增Id (自增字段Id)
	 * @throws Exception
	 */
	public int insertAndFecthAutoIdPrepareStatement(String sql, Object... vals) throws Exception {		
		Connection conn = getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		int id = 0;
		try {
			preparedStatement = conn.prepareStatement(sql);
			int index = 1;
			ObjStatement objStatement;
			for (Object val : vals) {
				objStatement = map.get(val.getClass());
				if (objStatement != null) {
					objStatement.set(index++, preparedStatement, val);
				}
				else {
					String msg = "未知类型[" + val.getClass() + "]";
					throw new Exception(msg);
				}
			}
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(rs, preparedStatement, conn);
		}
		return id;
	}
	
	/**
	 * 执行插入 CallableStatement, 并返回新增Id
	 * @param sql sql中第一个问号为返回值,其他问号为实际插入值
	 * @param vals
	 * @return
	 * 		新增Id (自增字段Id)
	 * @throws Exception
	 */
	public int insertAndFecthAutoIdCallableStatement(int outTyps, String sql, Object... vals) throws Exception {		
		Connection conn = getConnection();
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		int id = 0;
		try {
			callableStatement = conn.prepareCall(sql);
			int index = 1;
			callableStatement.registerOutParameter(1, outTyps);
			index++;
			ObjStatement objStatement;
			for (Object val : vals) {
				objStatement = map.get(val.getClass());
				if (objStatement != null) {
					objStatement.set(index++, callableStatement, val);
				}
				else {
					String msg = "未知类型[" + val.getClass() + "]";
					throw new Exception(msg);
				}
			}
			callableStatement.executeQuery();
			id = callableStatement.getInt(1);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(rs, callableStatement, conn);
		}
		return id;
	}

	/**
	 * 执行插入 PreparedStatement & CallableStatement
	 * @param sql
	 * @param vals
	 * @return
	 * 		插入影响的行数
	 * @throws Exception
	 */
	public int insertPrepareStatement(String sql, Object... vals) throws Exception {		
		Connection conn = getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			int index = 1;
			ObjStatement objStatement;
			for (Object val : vals) {
				objStatement = map.get(val.getClass());
				if (objStatement != null) {
					objStatement.set(index++, preparedStatement, val);
				}
				else {
					String msg = "未知类型[" + val.getClass() + "]";
					throw new Exception(msg);
				}
			}
			
			return preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closePreparedStatement(preparedStatement);
			closeConnection(conn);
		}
	}
	
	/**
	 * 查询 CallableStatement
	 * @param clazz
	 * @param sql
	 * @param vals
	 * @return
	 * 		一个 <T extends DaoRecord> 类型对象
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	protected <T extends DaoRecord> T selectCallableStatement(Class<T> clazz, String sql, Object... vals) throws Exception {		
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareCall(sql);
			
			int index = 1;
			ObjStatement stmtType = null;
			for (Object val : vals) {
				stmtType = map.get(val.getClass());
				if (stmtType != null) {
					stmtType.set(index++, stmt, val);
				}
				else {
					throw new Exception("selectCallableStatement: 未知类型[" + val.getClass() + "]");
				}
			}			
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				T record = clazz.newInstance();
				record.parseResultSet(rs);
				return record;
			}
			
			return null;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(rs, stmt, conn);
		}
	}
	
	/**
	 * 全量查询PreparedStatement
	 * 		-- 表数据太大的就不要使用了
	 * 		-- 最多量为 limit * 10, 超过就不继续load了
	 * @param limit
	 * @param clazz
	 * @param sql
	 * 		不能使用 limit 关键字, 里面会自动加上
	 * @param vals
	 * @return
	 * @throws Exception
	 */
	protected <T extends DaoRecord> List<T> allSelectPreparedStatement(final int limit, Class<T> clazz, String sql, Object... vals) throws Exception {		
		sql += " LIMIT ?,?";
		
		List<T> list = new ArrayList<T>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			
			int offset = 0;
			for (int i = 0; i < 10; i++) {
				try {
					stmt = conn.prepareStatement(sql);
					
					// fill stmt
					int index = 1;
					ObjStatement stmtType = null;
					for (Object val : vals) {
						stmtType = map.get(val.getClass());
						if (stmtType != null) {
							stmtType.set(index++, stmt, val);
						}
						else {
							throw new Exception("batchSelectCallableStatement: 未知类型[" + val.getClass() + "]");
						}
					}
					stmt.setInt(index++, offset);
					stmt.setInt(index++, limit);
					
					// query
					int recordCount = 0;
					rs = stmt.executeQuery();
					while (rs.next()) {
						T record = clazz.newInstance();
						record.parseResultSet(rs);
						list.add(record);
						recordCount++;
					}
					
					if (recordCount < limit) {
						break;
					}
					else {
						offset += limit;
					}
				}
				finally {
					close(rs, stmt, conn);
				}
			}
			
			return list;
		}
		catch (Exception e) {
			list = null;
			throw e;
		}
		finally {
			closeConnection(conn);
		}
	}
	
	/**
	 * 全量查询PreparedStatement
	 * 		-- 表数据太大的就不要使用了
	 * 		-- 最多量为 limit * 10, 超过就不继续load了
	 * @param limit
	 * @param clazz
	 * @param sql
	 * @param vals
	 * 		里面不要包含 offset、limit, 内部会自己计算 
	 * @return
	 * @throws Exception
	 */
	protected <T extends DaoRecord> List<T> allSelectCallableStatement(final int limit, Class<T> clazz, String sql, Object... vals) throws Exception {		
		Object[] newVals = new Object[vals.length + 2];
		System.arraycopy(vals, 0, newVals, 0, vals.length);
		
		int offset = 0;
		
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < 10; i++) {
			newVals[newVals.length - 2] = offset;
			newVals[newVals.length - 1] = limit;
			
			List<T> list = batchSelectCallableStatement(clazz, sql, newVals);
			result.addAll(list);
			
			if (list.size() < limit) {
				break;
			}
			else {
				offset += limit;
			}
		}
		
		return result;
	}
	
	/**
	 * 批量查询 CallableStatement
	 * @param clazz
	 * @param sql
	 * @param vals
	 * @return 一组 <T extends DaoRecord> 类型对象
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	protected <T extends DaoRecord> List<T> batchSelectCallableStatement(Class<T> clazz, String sql, Object... vals) throws Exception {
		
		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareCall(sql);
			
			int index = 1;
			ObjStatement stmtType = null;
			for (Object val : vals) {
				stmtType = map.get(val.getClass());
				if (stmtType != null) {
					stmtType.set(index++, stmt, val);
				}
				else {
					throw new Exception("batchSelectCallableStatement: 未知类型[" + val.getClass() + "]");
				}
			}			
			
			rs = stmt.executeQuery();
			
			List<T> list = new ArrayList<T>();
			while (rs.next()) {
				T record = clazz.newInstance();
				record.parseResultSet(rs);
				list.add(record);
			}
			
			return list;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(rs, stmt, conn);
		}
	}
	
	/**
	 * 依次关闭 Statement, Connection
	 * @param stmt
	 * @param conn
	 */
	public void close(Statement stmt, Connection conn) {
		close(null, stmt, conn);
	}

	/**
	 * 依次关闭 ResultSet, Statement, Connection
	 * @param rs
	 * @param stmt
	 * @param conn
	 */
	public void close(ResultSet rs, Statement stmt, Connection conn) {
		closeResultSet(rs);
		closePreparedStatement(stmt);
		closeConnection(conn);
	}

	/**
	 * 关闭 ResultSet
	 * @param rs
	 */
	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				Log.error("closeResultSet failed. msg=" + e.getMessage(), e);
			}
		}
	}

	/**
	 * 关闭 Statement & CallableStatement & PreparedStatement
	 * @param pstmt
	 */
	public void closePreparedStatement(Statement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			}
			catch (SQLException e) {
				Log.error("closePreparedStatement failed. msg=" + e.getMessage(), e);
			}
		}
	}

	/**
	 * 关闭 Connection
	 * @param conn
	 */
	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.getAutoCommit()) {
					conn.setAutoCommit(true);
				}

				conn.close();
			}
			catch (SQLException e) {
				Log.error("closeConnection failed. msg=" + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 把 column 的值 安全的解析成 Long 类型
	 * 		-- 如果字段不存在依然抛出异常
	 * @param rs
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static long parseTimestampToLong (ResultSet rs, String column) throws SQLException {
		try {
			Timestamp ts = rs.getTimestamp(column);
			if (ts == null) {
				return 0;
			}
			
			return ts.getTime();
		}
		catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().indexOf("not found") > -1) {
				// 该字段找不到
				throw e;
			}
			return 0;
		}
	}
}

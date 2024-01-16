package com.base.database.pool;

import com.base.database.DBParamWrapper;
import com.base.database.DBParameter;
import com.base.database.DBWatcher;
import com.base.database.DataExecutor;
import com.base.database.DataReader;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * DB帮助
 */
@Slf4j
public class DBHelper {


    private IDBPool pool;

    public DBHelper(IDBPool pool) {
        this.pool = pool;
    }

    /**
     * @param sql 执行的脚本
     * @return
     * @Action INSERT, UPDATE or DELETE
     */
    public int execNoneQuery(String sql) {
        return execNoneQuery(sql, null);
    }

    /**
     * @param sql    执行的脚本
     * @param params 脚本参数
     * @return
     * @Action INSERT, UPDATE or DELETE
     */
    public int execNoneQuery(String sql, DBParamWrapper params) {
        int result = -1;
        DBWatcher watcher = new DBWatcher();
        Connection conn = openConn();
        watcher.watchGetConnector();
        if (conn == null) {
            return result;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, getParams(params));
            result = pstmt.executeUpdate();
            watcher.watchSqlExec();
        } catch (SQLException e) {
            log.error("执行脚本出错", e);
        } finally {
            closeConn(conn, pstmt);
            watcher.keeyRecords(sql);
        }
        return result;
    }

    /**
     * 执行无参查询并返回单一记录
     *
     * @param sql    执行的脚本
     * @param reader 记录读取接口，实现单一记录读取过程
     * @return
     */
    public <T> T executeQuery(String sql, DataReader<T> reader) {
        return executeQuery(sql, null, reader);
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql     执行的脚本
     * @param params  脚本参数
     * @param reader  记录读取接口，实现单一记录读取过程
     * @param objects 额外传递的参数
     * @return
     */
    public <T> T executeQuery(String sql, DBParamWrapper params,
                              DataReader<T> reader, Object... objects) {
        DBWatcher watcher = new DBWatcher();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        T resultData = null;
        Connection conn = openConn();

        if (conn != null) {
            try {
                pstmt = conn.prepareStatement(sql);
                prepareCommand(pstmt, getParams(params));
                rs = pstmt.executeQuery();
                resultData = reader.readData(rs, objects);
            } catch (Exception e) {
                log.error("执行脚本出错", e);

            } finally {
                closeConn(conn, pstmt, rs);
                watcher.keeyRecords(sql);
            }
        }

        return resultData;
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql      执行的脚本
     * @param executor statement执行接口，实现单一记录读取过程
     * @return
     */
    public <T> T executeQuery(String sql, DataExecutor<T> executor) {
        return executeQuery(sql, null, executor);
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql      执行的脚本
     * @param params   脚本参数
     * @param executor statement执行接口，实现单一记录读取过程
     * @param objects  额外传递的参数
     * @return
     */
    public <T> T executeQuery(String sql, DBParamWrapper params, DataExecutor<T> executor, Object... objects) {
        DBWatcher watcher = new DBWatcher();
        PreparedStatement pstmt = null;
        T resultData = null;
        Connection conn = openConn();

        if (conn != null) {
            try {
                pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                prepareCommand(pstmt, getParams(params));
                resultData = executor.execute(pstmt, objects);
            } catch (Exception e) {
                log.error("执行脚本出错", e);

            } finally {
                closeConn(conn, pstmt);
                watcher.keeyRecords(sql);
            }
        }

        return resultData;
    }

    /**
     * @param sql      执行批量处理的脚本
     * @param entities 实体集合
     * @param executor 回调
     * @return
     */
    public <T, V> T sqlBatch(String sql, List<V> entities,
                             DataExecutor<T> executor) {
        DBWatcher watcher = new DBWatcher();
        Connection conn = openConn();
        watcher.watchGetConnector();
        if (conn == null) {
            return null;
        }
        PreparedStatement pstmt = null;
        try {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            T result = executor.execute(pstmt, entities);
            conn.commit();
            return result;

        } catch (Exception e) {
            log.error("执行批量脚本出错", e);

        } finally {
            closeConn(conn, pstmt);
            watcher.keeyRecords(sql);
        }
        return null;
    }

    /**
     * @param sqlComm 执行的脚本
     * @return 返回影响行数
     * @Action INSERT_Batch, UPDATE_Batch or DELETE_Batch
     */
    public int sqlBatch(List<String> sqlComm) {
        int[] results = null;
        int result = -1;
        Connection conn = openConn();

        if (conn == null) {
            return result;
        }
        Statement stmt = null;
        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            for (int i = 0; i < sqlComm.size(); i++) {
                stmt.addBatch(sqlComm.get(i));
            }
            results = stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            log.error("执行批量脚本出错", e);
        } finally {
            closeConn(conn, stmt);
        }
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                result += results[i];
            }
        }
        return result;
    }

    /**
     * 给Statement赋值
     *
     * @param pstmt
     * @param parms
     * @throws SQLException
     */
    public PreparedStatement prepareCommand(PreparedStatement pstmt, Map<Integer, DBParameter> parms) throws SQLException {
        if (pstmt == null || parms == null) {
            return null;
        }
        for (Map.Entry<Integer, DBParameter> entry : parms.entrySet()) {
            pstmt.setObject(entry.getKey(), entry.getValue().getResult());
        }

        return pstmt;
    }

    /**
     * 获取连接池的连接
     *
     * @return
     */
    private Connection openConn() {
        return pool.getConnection();
    }

    private Map<Integer, DBParameter> getParams(DBParamWrapper paramWrapper) {
        Map<Integer, DBParameter> params = null;
        if (paramWrapper != null) {
            params = paramWrapper.getParams();
        }
        return params;
    }

    /**
     * 关闭Connection、Statem和ResultSet
     *
     * @param conn
     * @param stmt
     * @param rs
     */
    private void closeConn(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null && rs.isClosed() == false) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            log.error("关闭Resultset出错", e);
        } finally {
            closeConn(conn, stmt);
        }

    }

    /**
     * 关闭Conne和Statement
     *
     * @param conn
     * @param stmt
     */
    private void closeConn(Connection conn, Statement stmt) {
        try {
            if (stmt != null && stmt.isClosed() == false) {
                if (stmt instanceof PreparedStatement) {
                    ((PreparedStatement) stmt).clearParameters();
                }
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
            log.error("关闭statement出错", e);
        } finally {
            closeConn(conn);
        }
    }

    /**
     * 关闭Connection
     *
     * @param conn
     */
    private void closeConn(Connection conn) {

        try {
            if (conn != null && conn.isClosed() == false) {
                conn.setAutoCommit(true);
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            log.error("关闭数据库连接出错", e);
        }
    }
}

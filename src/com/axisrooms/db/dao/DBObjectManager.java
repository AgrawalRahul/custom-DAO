package com.axisrooms.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import com.axisrooms.db.ConnectionCounter;

/**
 * @author dj
 * 
 */
public class DBObjectManager {

    public static final SimpleDateFormat SqlDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * private constructor this is a utility class should not be instantiated
     */
    private DBObjectManager() {
    }

    /**
     * Function to retieve a list of objects from the DB given a Object type,
     * connection, SQL Query and Whether or not to close the connection after
     * this use
     * 
     * @param <E> The Class of the correct type that can be re-created from the
     *            DB using SQL select
     * @param t
     * @param conn
     * @param sql
     * @param closeConn
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    // public static <E extends DbObject> List<E>
    // getSelectFromConnection(Class<E> t, Connection conn, String sql,
    // boolean closeConn) throws SQLException, InstantiationException,
    // IllegalAccessException {
    // PreparedStatement stmt = null;
    // List<E> results = new ArrayList<E>();
    // try {
    // stmt = conn.prepareStatement(sql);
    // results = getSelectFromStatement(t, stmt, true);
    // } finally {
    // if (closeConn && conn != null) {
    // try {
    // conn.close();
    // } catch (SQLException e) {
    // }
    // conn = null;
    // }
    // }
    // return results;
    // }

    // public static <E extends DbObject> List<E>
    // getSelectFromStatement(Class<E> t, PreparedStatement stmt,
    // boolean closeStmt) throws SQLException, InstantiationException,
    // IllegalAccessException {
    //
    // ResultSet rs = null;
    // List<E> results = new ArrayList<E>();
    // try {
    // rs = stmt.executeQuery();
    // while (rs.next()) {
    // E e = t.newInstance();
    // e.populateFromDataSet(rs);
    // results.add(e);
    // }
    // } finally {
    // if (rs != null) {
    // try {
    // rs.close();
    // } catch (SQLException e) {
    // }
    // rs = null;
    // }
    // if (closeStmt && stmt != null) {
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // }
    // stmt = null;
    // }
    // }
    // return results;
    // }

    public static void closeResources(ResultSet rs, Statement stmt, Connection conn, boolean closeConn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn, closeConn);

    }

    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn, true);
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                // do nothing
            }
            rs = null;
        }
    }

    public static void closeConnection(Connection conn, boolean closeConn) {
        if (conn != null && closeConn) {
            try {
                synchronized (ConnectionCounter.class) {
                    ConnectionCounter.numberOfConnections--;
                }
                conn.close();
            } catch (Exception e) {
                // do nothing
            }
            conn = null;
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                // do nothing
            }
            stmt = null;
        }
    }
}
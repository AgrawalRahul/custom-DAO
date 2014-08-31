package com.axisrooms.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.axisrooms.db.query.SqlQuery;

/**
 * This Class is to handle all DB operations To execute a query simple code
 * should DBManager.getInstance().execute(query)
 * 
 * @author vikas garg
 * 
 */
public class QueryManager {

    private Connection          m_conn                = null;
    private boolean             m_isAutoCommitEnabled = true;
    private static final Logger s_logger              = Logger.getLogger(QueryManager.class);

    private QueryManager() {
    }

    public static QueryManager getInstance() {
        s_logger.debug("Start: Creating QueryManager.");
        QueryManager queryManager = new QueryManager();
        s_logger.debug("End: Creating QueryManager.");
        return queryManager;
    }

    public boolean execute(SqlQuery query) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean executeResult = true;
        s_logger.debug("Executing query " + query.getQueryString() + " from query class "
                + query.getClass().getSimpleName());
        if (m_conn == null) {
            m_conn = AccountsDatabaseManager.getConnection(m_isAutoCommitEnabled);
        }
        try {
            pstmt = m_conn.prepareStatement(query.getQueryString());
            query.setQueryParameters(pstmt);
            s_logger.debug("Executing Prepared Statement " + pstmt.toString() + " from query class "
                    + query.getClass().getSimpleName());
            pstmt.execute();
            do {
                rs = pstmt.getResultSet();
                int updateCount = pstmt.getUpdateCount();
                query.processResultSet(rs, updateCount);
            } while (pstmt.getMoreResults());
        } catch (Exception e) {
            s_logger.error("Exception occured while executing query: " + pstmt, e);
            executeResult = false;
            throw e;
        } finally {
            if (m_conn != null && m_isAutoCommitEnabled) {
                s_logger.debug("Closing connection");
                synchronized (ConnectionCounter.class) {
                    ConnectionCounter.numberOfConnections--;
                }
                m_conn.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return executeResult;
    }

    /**
     * Only used in case of batch query... this code is a hack and so please
     * bear with it till we make batch insert query.
     * 
     * @param sqlQuery
     * @return
     * @throws Exception
     */
    public PreparedStatement getPreparedStatementForBatch(String sqlQuery) throws Exception {
        return m_conn.prepareStatement(sqlQuery);
    }

    public void beginTransaction() throws Exception {
        if (m_conn == null) {
            m_conn = AccountsDatabaseManager.getConnection(false);
        } else {
            m_conn.setAutoCommit(false);
        }
        m_isAutoCommitEnabled = false;
        s_logger.debug("SQL Transaction Begins");
    }

    public void endTransaction() throws Exception {
        if (m_conn != null) {
            try {
                m_conn.commit();
            } catch (SQLException e) {
                s_logger.error("Error committing the connection", e);
                m_conn.rollback();
                throw e;
            } finally {
                s_logger.debug("Closing connection");
                synchronized (ConnectionCounter.class) {
                    ConnectionCounter.numberOfConnections--;
                }
                m_conn.close();
            }
        }
        s_logger.debug("SQL Transaction Ends");
    }

    public void rollbackTransaction() throws Exception {
        if (m_conn != null) {
            try {
                m_conn.rollback();
            } catch (SQLException e) {
                s_logger.error("Error reverting the connection", e);
                m_conn.rollback();
                throw e;
            } finally {
                s_logger.debug("Closing connection");
                synchronized (ConnectionCounter.class) {
                    ConnectionCounter.numberOfConnections--;
                }
                m_conn.close();
            }
        }
        s_logger.debug("SQL Transaction Rollback");
    }

}
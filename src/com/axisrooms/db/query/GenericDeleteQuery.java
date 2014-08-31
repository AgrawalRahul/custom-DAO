package com.axisrooms.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.axisrooms.db.dao.ARDaoManager;
import com.axisrooms.db.dao.DBObjectManager;
import com.axisrooms.db.query.generic.filter.GenericFilter;

public class GenericDeleteQuery implements SqlQuery {

    private static final Logger s_logger = Logger.getLogger(GenericUpdateQuery.class);

    private String              m_tableName;
    private String              m_joinIdentifier;
    private QueryCheck          m_check;

    private List<GenericFilter> m_genericFilters;

    private List<SqlQuery>      m_childQueries;

    @Deprecated
    public GenericDeleteQuery(String tableName, String joinIdentifier) {
        m_tableName = tableName;
        m_joinIdentifier = joinIdentifier;
    }

    public GenericDeleteQuery(String tableName, boolean isNumberOfRowsCheck, int numberOfRowsChanged) {
        m_tableName = tableName;
        m_joinIdentifier = "";
        m_check = new QueryCheck(isNumberOfRowsCheck, numberOfRowsChanged);
    }

    public String getTableName() {
        return m_tableName;
    }

    public void setTableName(String tableName) {
        m_tableName = tableName;
    }

    public String getJoinIdentifier() {
        return m_joinIdentifier;
    }

    public void setJoinIdentifier(String joinIdentifier) {
        m_joinIdentifier = joinIdentifier;
    }

    public List<GenericFilter> getGenericFilters() {
        if (m_genericFilters == null) {
            m_genericFilters = new ArrayList<GenericFilter>();
        }
        return m_genericFilters;
    }

    public void setGenericFilters(List<GenericFilter> genericFilters) {
        m_genericFilters = genericFilters;
    }

    public void addGenericFilter(GenericFilter filter) {
        this.getGenericFilters().add(filter);
    }

    public String getFromString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTableName() + " " + getJoinIdentifier());
        return sb.toString();
    }

    public String getWhereClauseString() {
        StringBuilder sb = new StringBuilder();
        if (this.getGenericFilters() != null) {
            for (GenericFilter filter : this.getGenericFilters()) {
                sb.append(" and ");
                filter.appendPreparedStatementString(sb, this.getJoinIdentifier());
            }
        }
        return sb.toString();
    }

    public String getQueryString() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(getTableName());
        sb.append(" where true ");
        sb.append(getWhereClauseString());
        return sb.toString();
    }

    public int setWhereParams(PreparedStatement psmt, int index) throws Exception {
        for (GenericFilter columnSetter : this.getGenericFilters()) {
            index = columnSetter.appendPreparedStatementValue(psmt, index++);
        }
        return index;
    }

    @Deprecated
    public boolean runQuery(Connection connection, boolean isNumberOfRowsCheck, int numberOfRowsChanged) {
        m_check = new QueryCheck(isNumberOfRowsCheck, numberOfRowsChanged);
        return runQuery(connection);
    }

    public boolean runQuery(Connection connection) {
        if (getCheck() == null) {
            m_check = new QueryCheck(false, -1);
        }
        if (connection == null) {
            return ARDaoManager.getInstance().executeQuery(this);
        }
        // If there is connection.. big chance that it has been passed to do
        // some work which can be reverted later..
        Connection conn = connection;
        PreparedStatement psmt = null;
        boolean closeConn = false;
        boolean success = true;
        try {
            psmt = conn.prepareStatement(this.getQueryString());
            int index = 1;
            setWhereParams(psmt, index);
            int numberOfRows = psmt.executeUpdate();
            if (this.getCheck().isNumberOfRowsCheck()) {
                success = (this.getCheck().getNumberOfRowsChanged() == numberOfRows);
            }
            if (!success) {
                throw new Exception("number of rows changed did not match");
            }
        } catch (Exception e) {
            success = false;
            s_logger.error(e.getMessage(), e);
        } finally {
            DBObjectManager.closeResources(null, psmt, conn, closeConn);
        }
        return success;
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
        setWhereParams(pstmt, 1);
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        if (!(!this.getCheck().isNumberOfRowsCheck() || (this.getCheck().isNumberOfRowsCheck() && (this.getCheck()
                .getNumberOfRowsChanged() == updateCount)))) {
            throw new Exception("delete count not satisfied.");
        }
    }

    public static class QueryCheck {

        private boolean isNumberOfRowsCheck;
        private int     numberOfRowsChanged;

        public QueryCheck(boolean isNumberOfRowsCheck2, int numberOfRowsChanged2) {
            this.isNumberOfRowsCheck = isNumberOfRowsCheck2;
            this.numberOfRowsChanged = numberOfRowsChanged2;
        }

        public boolean isNumberOfRowsCheck() {
            return isNumberOfRowsCheck;
        }

        public void setNumberOfRowsCheck(boolean isNumberOfRowsCheck) {
            this.isNumberOfRowsCheck = isNumberOfRowsCheck;
        }

        public int getNumberOfRowsChanged() {
            return numberOfRowsChanged;
        }

        public void setNumberOfRowsChanged(int numberOfRowsChanged) {
            this.numberOfRowsChanged = numberOfRowsChanged;
        }
    }

    public QueryCheck getCheck() {
        return m_check;
    }

    public void setCheck(QueryCheck check) {
        m_check = check;
    }

    @Override
    public List<SqlQuery> getChildQueries() {
        if (m_childQueries == null) {
            m_childQueries = new ArrayList<SqlQuery>();
        }
        return m_childQueries;
    }

    public void setChildQueries(List<SqlQuery> childQueries) {
        m_childQueries = childQueries;
    }

    public void addChildQuery(SqlQuery query) {
        getChildQueries().add(query);
    }

}

package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.axisrooms.db.query.generic.filter.GenericFilter;

public class GenericUpdateQuery implements SqlQuery {

    private String              m_tableName;
    private String              m_joinIdentifier;

    private List<GenericFilter> m_genericSetters;
    private List<GenericFilter> m_genericFilters;

    private QueryCheck          m_check;
    private boolean             m_isAutoCommit = true;

    private List<SqlQuery>      m_childQueries;

    @Deprecated
    public GenericUpdateQuery(String tableName, String joinIdentifier) {
        m_tableName = tableName;
        m_joinIdentifier = joinIdentifier;
    }

    public GenericUpdateQuery(String tableName, boolean isNumberOfRowsCheck, int numberOfRowsChanged) {
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

    public List<GenericFilter> getGenericSetters() {
        if (m_genericSetters == null) {
            m_genericSetters = new ArrayList<GenericFilter>();
        }
        return m_genericSetters;
    }

    public void setGenericSetters(List<GenericFilter> genericSetters) {
        m_genericSetters = genericSetters;
    }

    public void addGenericSetter(GenericFilter filter) {
        this.getGenericSetters().add(filter);
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

    public String getSetString() {
        if (getGenericFilters() == null || getGenericFilters().size() == 0) {
            throw new IllegalAccessError(
                    "GenericUpdateQuery: You have to set update columns for any updation operation");
        }
        StringBuilder sb = new StringBuilder();
        boolean firstTime = false;
        for (GenericFilter columnSetter : this.getGenericSetters()) {
            if (firstTime) {
                sb.append(", ");
            } else {
                firstTime = true;
            }
            columnSetter.appendPreparedStatementString(sb, this.getJoinIdentifier());
        }
        return sb.toString();
    }

    public String getWhereClauseString() {
        if (getGenericFilters() == null || getGenericFilters().size() == 0) {
            throw new IllegalAccessError("GenericUpdateQuery: You have to set check columns for any updation operation");
        }
        StringBuilder sb = new StringBuilder();
        for (GenericFilter filter : this.getGenericFilters()) {
            sb.append("and ");
            filter.appendPreparedStatementString(sb, this.getJoinIdentifier());
        }
        return sb.toString();
    }

    public String getQueryString() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(getTableName());
        sb.append(" set ");
        sb.append(this.getSetString());
        sb.append(" where true ");
        sb.append(getWhereClauseString());
        return sb.toString();
    }

    public int setSetParams(PreparedStatement psmt, int index) throws Exception {
        for (GenericFilter columnSetter : this.getGenericSetters()) {
            index = columnSetter.appendPreparedStatementValue(psmt, index++);
        }
        return index;
    }

    public int setWhereParams(PreparedStatement psmt, int index) throws Exception {
        for (GenericFilter columnSetter : this.getGenericFilters()) {
            index = columnSetter.appendPreparedStatementValue(psmt, index++);
        }
        return index;
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

    public boolean isAutoCommit() {
        return m_isAutoCommit;
    }

    public void setAutoCommit(boolean isAutoCommit) {
        m_isAutoCommit = isAutoCommit;
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
        int index = 1;
        index = setSetParams(pstmt, index);
        setWhereParams(pstmt, index);
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        if (!(!this.getCheck().isNumberOfRowsCheck() || (this.getCheck().isNumberOfRowsCheck() && this.getCheck()
                .getNumberOfRowsChanged() == updateCount))) {
            throw new Exception("Update count not satisfied.");
        }
    }

}

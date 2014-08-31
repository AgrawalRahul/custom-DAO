package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ExecuteSequenceQuery implements SqlQuery {
    private long   m_nextId = -1;
    private String m_sequenceName;

    @Override
    public String getQueryString() throws Exception {
        return "select nextval('" + getSequenceName() + "')";
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        if (rs.next()) {
            setNextId(rs.getLong(1));
        }
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
    }

    public long getNextId() {
        return m_nextId;
    }

    public void setNextId(long nextId) {
        this.m_nextId = nextId;
    }

    @Override
    public List<SqlQuery> getChildQueries() {
        return null;
    }

    public String getSequenceName() {
        return m_sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        m_sequenceName = sequenceName;
    }

}
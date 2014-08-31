package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.axisrooms.db.column.ColumnInterface;

public abstract class AbstractInsertQuery<T> implements SqlQuery {
    protected static final Logger s_logger            = Logger.getLogger(AbstractInsertQuery.class);
    private List<ColumnInterface<T>> m_columns           = null;
    private NextIdQuery           m_nextIdQuery       = null;
    private List<SqlQuery>        m_nextInsertQueries = null;

    public class NextIdQuery implements SqlQuery {
        private long m_nextId = -1;

        @Override
        public String getQueryString() throws Exception {
            return "select nextval('" + getSequenceName() + "')";
        }

        @Override
        public String getTableName() {
            return AbstractInsertQuery.this.getTableName();
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

    }

    public abstract String getSequenceName();

    public abstract List<ColumnInterface<T>> getInsertColumns();

    public abstract void processNextId();

    public abstract T getObjectToInsert();

    public abstract void setObjectToInsert(T object);

    /**
     * This method returns the insert SQL. In future this should be improved so
     * that the string is not computed every time. Maximum time the insert SQL
     * will be same so we can use a static variable and store it.
     */
    @Override
    public String getQueryString() throws Exception {
        StringBuilder sb = new StringBuilder();
        StringBuilder endSb = new StringBuilder(") values(");
        sb.append("insert into ").append(getTableName()).append("(");
        boolean and = false;
        for (ColumnInterface column : getInsertColumns()) {
            if (and) {
                sb.append(COMMA).append(SPACE);
                endSb.append(COMMA);
            }
            sb.append(column.getColumnName());
            endSb.append(VALUE_HOLDER);
            and = true;
        }
        endSb.append(");");
        sb.append(endSb);
        return sb.toString();
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
        s_logger.debug("Start: Set query parameters.");
        if (getNextIdQuery() != null) {
            processNextId();
        }
        int i = 1;
        for (ColumnInterface column : getInsertColumns()) {
            column.setInPreparedStatement(getObjectToInsert(), pstmt, i++);
        }
        s_logger.debug("End: Set query parameters.");
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        s_logger.debug("Start: Processing Result Set.");
        s_logger.debug("Update Count: " + updateCount);
        if (updateCount == 0) {
            throw new RuntimeException("Entity could not be added for: " + this.getClass().getSimpleName());
        } else if (updateCount > 1) {
            throw new RuntimeException("Fatal Exception update count more than 1 in insert Entity"
                    + this.getClass().getSimpleName());
        }
        s_logger.debug("End: Processing Result Set.");
    }

    public NextIdQuery getNextIdQuery() {
        return m_nextIdQuery;
    }

    public void setNextIdQuery(NextIdQuery nextIdQuery) {
        this.m_nextIdQuery = nextIdQuery;
    }

    public List<ColumnInterface<T>> getColumns() {
        return m_columns;
    }

    public void setColumns(List<ColumnInterface<T>> columns) {
        this.m_columns = columns;
    }

    public List<SqlQuery> getNextInsertQueries() {
        if (m_nextInsertQueries == null) {
            m_nextInsertQueries = new ArrayList<SqlQuery>();
        }
        return m_nextInsertQueries;
    }

    public void addNextInsertQuery(SqlQuery insertQuery) {
        getNextInsertQueries().add(insertQuery);
    }

    @Override
    public List<SqlQuery> getChildQueries() {
        return m_nextInsertQueries;
    }
}

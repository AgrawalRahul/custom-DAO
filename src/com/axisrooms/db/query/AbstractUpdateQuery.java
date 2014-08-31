package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;

public abstract class AbstractUpdateQuery implements SqlQuery {

    private List<ColumnInterface> m_updateColumns;
    private List<ColumnInterface> m_whereColumns;
    private boolean               m_uniqueUpdate    = false;
    private AbstractUpdateQuery   m_nextUpdateQuery = null;

    public abstract Object getUpdateObject();

    @Override
    public String getQueryString() throws Exception {
        if (getUpdateColumns() == null || getUpdateColumns().size() == 0 || getWhereColumns() == null
                || getWhereColumns().size() == 0) {
            throw new IllegalAccessException("You have to set update and check columns for any updation operation");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getTableName()).append(" set ");
        boolean and = false;
        for (ColumnInterface column : getUpdateColumns()) {
            sb.append(and ? COMMA : EMPTY).append(column.getColumnName()).append(EQUAL).append(VALUE_HOLDER);
            and = true;
        }
        and = false;
        sb.append(" where");
        for (ColumnInterface column : getWhereColumns()) {
            sb.append(and ? SQL_AND : SPACE).append(column.getColumnName()).append(EQUAL).append(VALUE_HOLDER);
            and = true;
        }
        return sb.toString();
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
        int i = 1;
        for (ColumnInterface column : getUpdateColumns()) {
            column.setInPreparedStatement(getUpdateObject(), pstmt, i++);
        }
        for (ColumnInterface column : getWhereColumns()) {
            column.setInPreparedStatement(getUpdateObject(), pstmt, i++);
        }
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        if (updateCount == 0) {
            throw new RuntimeException("Object could not be updated: " + this.getClass().getSimpleName());
        } else if (isUniqueUpdate() && updateCount > 1) {
            throw new RuntimeException("Fatal Exception update count=" + updateCount + " for "
                    + this.getClass().getSimpleName());
        }
    }

    public List<ColumnInterface> getUpdateColumns() {
        return m_updateColumns;
    }

    public void setUpdateColumns(List<ColumnInterface> updateColumns) {
        this.m_updateColumns = updateColumns;
    }

    public List<ColumnInterface> getWhereColumns() {
        return m_whereColumns;
    }

    public void setWhereColumns(List<ColumnInterface> whereColumns) {
        this.m_whereColumns = whereColumns;
    }

    public boolean isUniqueUpdate() {
        return m_uniqueUpdate;
    }

    public void setUniqueUpdate(boolean uniqueUpdate) {
        this.m_uniqueUpdate = uniqueUpdate;
    }

    public AbstractUpdateQuery getNextUpdateQuery() {
        return m_nextUpdateQuery;
    }

    public void setNextUpdateQuery(AbstractUpdateQuery nextUpdateQuery) {
        this.m_nextUpdateQuery = nextUpdateQuery;
    }
}

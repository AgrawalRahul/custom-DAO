package com.axisrooms.db.query;

import java.util.List;

import com.axisrooms.db.column.ColumnInterface;

public class GenericInsertQuery {

    private String                m_tableName;
    private String                m_sequenceName;
    private List<ColumnInterface> m_columns;
    private Object                m_objectToInsert;

    public String getTableName() {
        return m_tableName;
    }

    public void setTableName(String tableName) {
        m_tableName = tableName;
    }

    public String getSequenceName() {
        return m_sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        m_sequenceName = sequenceName;
    }

    public List<ColumnInterface> getColumns() {
        return m_columns;
    }

    public void setColumns(List<ColumnInterface> columns) {
        m_columns = columns;
    }

    public Object getObjectToInsert() {
        return m_objectToInsert;
    }

    public void setObjectToInsert(Object objectToInsert) {
        m_objectToInsert = objectToInsert;
    }

}

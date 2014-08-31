package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;

public class GenericBooleanFilter implements GenericFilter {

    private ColumnInterface m_column;
    private boolean         m_data;

    public GenericBooleanFilter(ColumnInterface column, boolean data) {
        m_column = column;
        m_data = data;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier)).append(" = ").append(isData())
                .append(" ");
    }

    public ColumnInterface getColumn() {
        return m_column;
    }

    public void setColumn(ColumnInterface column) {
        m_column = column;
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" = ?");

    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        psmt.setBoolean(index++, this.isData());
        return index;
    }

    public void setData(boolean data) {
        m_data = data;
    }

    public boolean isData() {
        return m_data;
    }

}

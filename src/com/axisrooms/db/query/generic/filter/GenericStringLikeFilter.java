package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;

public class GenericStringLikeFilter implements GenericFilter {

    private ColumnInterface m_column;
    private String          m_data;

    public GenericStringLikeFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericStringLikeFilter(ColumnInterface column, String data) {
        m_column = column;
        m_data = data;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != null) {
            sbuf.append(" and ").append(this.getColumn().getColumnName(joinIdentifier)).append(" ilike '%")
                    .append(getData()).append("%' ");
        }
    }

    public ColumnInterface getColumn() {
        return m_column;
    }

    public void setColumn(ColumnInterface column) {
        m_column = column;
    }

    public String getData() {
        return m_data;
    }

    public void setData(String data) {
        m_data = data;
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != null) {
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" = ?");
        }
    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        if (getData() != null) {
            psmt.setString(index++, "%" + this.getData() + "%");
        }
        return index;
    }

}

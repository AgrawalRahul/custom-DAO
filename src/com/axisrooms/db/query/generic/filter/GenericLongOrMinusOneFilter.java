package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;

public class GenericLongOrMinusOneFilter implements GenericFilter {

    private ColumnInterface m_column;
    private long            m_data = Long.MAX_VALUE;

    public GenericLongOrMinusOneFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericLongOrMinusOneFilter(ColumnInterface column, long data) {
        m_column = column;
        m_data = data;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Long.MAX_VALUE) {
            sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier)).append(" in (").append(getData())
                    .append(" ,-1 )");
        }
    }

    public ColumnInterface getColumn() {
        return m_column;
    }

    public void setColumn(ColumnInterface column) {
        m_column = column;
    }

    public long getData() {
        return m_data;
    }

    public void setData(long data) {
        m_data = data;
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Long.MAX_VALUE) {
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" = (? or -1)");
        }

    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        if (getData() != Long.MAX_VALUE) {
            psmt.setLong(index++, this.getData());
        }
        return index;
    }

}

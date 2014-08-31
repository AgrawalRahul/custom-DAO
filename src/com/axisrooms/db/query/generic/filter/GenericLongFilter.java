package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Sign;

public class GenericLongFilter implements GenericFilter {

    private ColumnInterface m_column;
    private long            m_data = Long.MAX_VALUE;
    private List<Long>      m_dataSet;
    private Sign            m_sign = Sign.EQUALS;

    public GenericLongFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericLongFilter(ColumnInterface column, long data) {
        m_column = column;
        m_data = data;
    }

    public GenericLongFilter(ColumnInterface column, long data, Sign sign) {
        m_column = column;
        m_data = data;
        m_sign = sign;
    }

    public GenericLongFilter(ColumnInterface column, List<Long> dataSet) {
        m_column = column;
        m_dataSet = dataSet;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Long.MAX_VALUE) {
            sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier))
                    .append(" " + getSign().getSign() + " ").append(getData()).append(" ");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (Long data : this.getDataSet()) {
                if (comma) {
                    sbuf.append(",");
                }
                sbuf.append(data);
                comma = true;
            }
            sbuf.append(") ");
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

    public List<Long> getDataSet() {
        return m_dataSet;
    }

    public void setDataSet(List<Long> dataSet) {
        m_dataSet = dataSet;
    }

    public Sign getSign() {
        return m_sign;
    }

    public void setSign(Sign sign) {
        m_sign = sign;
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Long.MAX_VALUE) {
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" " + getSign().getSign() + " ?");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (@SuppressWarnings("unused")
            Long data : this.getDataSet()) {
                if (comma) {
                    sbuf.append(",");
                }
                sbuf.append("?");
                comma = true;
            }
            sbuf.append(") ");
        }

    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        if (getData() != Long.MAX_VALUE) {
            psmt.setLong(index++, this.getData());
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            for (Long data : this.getDataSet()) {
                psmt.setLong(index++, data);
            }
        }
        return index;
    }

}

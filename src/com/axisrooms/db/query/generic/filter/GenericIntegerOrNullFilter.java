package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Sign;

public class GenericIntegerOrNullFilter implements GenericFilter {

    private ColumnInterface m_column;
    private int             m_data = Integer.MAX_VALUE;
    private List<Integer>   m_dataSet;
    private Sign            m_sign = Sign.EQUALS;

    public GenericIntegerOrNullFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericIntegerOrNullFilter(ColumnInterface column, int data) {
        m_column = column;
        m_data = data;
    }

    public GenericIntegerOrNullFilter(ColumnInterface column, int data, Sign sign) {
        m_column = column;
        m_data = data;
        m_sign = sign;
    }

    public GenericIntegerOrNullFilter(ColumnInterface column, List<Integer> dataSet) {
        m_column = column;
        m_dataSet = dataSet;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Integer.MAX_VALUE) {
            sbuf.append(" and (").append(this.getColumn().getColumnName(joinIdentifier))
                    .append(" " + getSign().getSign() + " ").append(getData()).append(" or ")
                    .append(this.getColumn().getColumnName(joinIdentifier)).append(" is null ) ");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(" and (").append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (Integer data : this.getDataSet()) {
                if (comma) {
                    sbuf.append(",");
                }
                sbuf.append(data);
                comma = true;
            }
            sbuf.append(") ").append(" or ").append(this.getColumn().getColumnName(joinIdentifier))
                    .append(" is null ) ");
            ;
        }
    }

    public ColumnInterface getColumn() {
        return m_column;
    }

    public void setColumn(ColumnInterface column) {
        m_column = column;
    }

    public int getData() {
        return m_data;
    }

    public void setData(int data) {
        m_data = data;
    }

    public List<Integer> getDataSet() {
        return m_dataSet;
    }

    public void setDataSet(List<Integer> dataSet) {
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
        if (getData() != Integer.MAX_VALUE) {
            sbuf.append("(").append(this.getColumn().getColumnName(joinIdentifier))
                    .append(" " + getSign().getSign() + " ?").append(" or ")
                    .append(this.getColumn().getColumnName(joinIdentifier)).append(" is null ) ");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append("(").append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (@SuppressWarnings("unused")
            Integer data : this.getDataSet()) {
                if (comma) {
                    sbuf.append(",");
                }
                sbuf.append("?");
                comma = true;
            }
            sbuf.append(") ").append(" or ").append(this.getColumn().getColumnName(joinIdentifier))
                    .append(" is null ) ");
            ;
        }

    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        if (getData() != Integer.MAX_VALUE) {
            psmt.setInt(index++, this.getData());
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            for (Integer data : this.getDataSet()) {
                psmt.setInt(index++, data);
            }
        }
        return index;
    }

}

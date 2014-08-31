package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Sign;

public class GenericDoubleFilter implements GenericFilter {

    private ColumnInterface m_column;
    private double          m_data = Double.MAX_VALUE;
    private List<Double>    m_dataSet;
    private Sign            m_sign = Sign.EQUALS;

    public GenericDoubleFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericDoubleFilter(ColumnInterface column, double data) {
        m_column = column;
        m_data = data;
    }

    public GenericDoubleFilter(ColumnInterface column, double data, Sign sign) {
        m_column = column;
        m_data = data;
        m_sign = sign;
    }

    public GenericDoubleFilter(ColumnInterface column, List<Double> dataSet) {
        m_column = column;
        m_dataSet = dataSet;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != Double.MAX_VALUE) {
            sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier))
                    .append(" " + getSign().getSign() + " ").append(getData()).append(" ");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(" and " + this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (Double data : this.getDataSet()) {
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

    public double getData() {
        return m_data;
    }

    public void setData(double data) {
        m_data = data;
    }

    public List<Double> getDataSet() {
        return m_dataSet;
    }

    public void setDataSet(List<Double> dataSet) {
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
        if (getData() != Double.MAX_VALUE) {
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" " + getSign().getSign() + " ?");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (@SuppressWarnings("unused")
            Double data : this.getDataSet()) {
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
        if (getData() != Double.MAX_VALUE) {
            psmt.setDouble(index++, this.getData());
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            for (Double data : this.getDataSet()) {
                psmt.setDouble(index++, data);
            }
        }
        return index;
    }

}

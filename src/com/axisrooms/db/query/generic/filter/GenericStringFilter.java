package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;

public class GenericStringFilter implements GenericFilter {

    private ColumnInterface m_column;
    private List<String>    m_dataSet;
    private String          m_data;

    public GenericStringFilter(ColumnInterface column) {
        m_column = column;
    }

    public GenericStringFilter(ColumnInterface column, String data) {
        m_column = column;
        m_data = data;
    }

    public GenericStringFilter(ColumnInterface column, List<String> dataSet) {
        m_column = column;
        m_dataSet = dataSet;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        if (getData() != null) {
            sbuf.append(" and ").append(this.getColumn().getColumnName(joinIdentifier)).append(" = '")
                    .append(getData()).append("' ");
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(" and ").append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (String data : this.getDataSet()) {
                if (comma) {
                    sbuf.append(",");
                }
                sbuf.append("'").append(data).append("'");
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

    public List<String> getDataSet() {
        return m_dataSet;
    }

    public void setDataSet(List<String> dataSet) {
        m_dataSet = dataSet;
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
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            boolean comma = false;
            sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" in  (");
            for (@SuppressWarnings("unused") String data : this.getDataSet()) {
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
        if (getData() != null) {
            psmt.setString(index++, this.getData());
        }
        if (this.getDataSet() != null && this.getDataSet().size() > 0) {
            for (String data : this.getDataSet()) {
                psmt.setString(index++, data);
            }
        }
        return index;
    }

}

package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Sign;

public class GenericColumnFilter implements GenericFilter {

    private ColumnInterface m_column1;
    private ColumnInterface m_coulmn2;
    private Sign            m_sign;

    public GenericColumnFilter(ColumnInterface column1, ColumnInterface column2, Sign sign) {
        m_column1 = column1;
        m_coulmn2 = column2;
        m_sign = sign;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(" and ").append(this.getColumn1().getColumnName(joinIdentifier))
                .append(" " + getSign().getSign() + " ").append(this.getColumn1().getColumnName(joinIdentifier))
                .append(" ");
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(this.getColumn1().getColumnName(joinIdentifier)).append(" " + getSign().getSign() + " ")
                .append(this.getColumn1().getColumnName(joinIdentifier)).append(" ");
    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        return index;
    }

    public ColumnInterface getColumn1() {
        return m_column1;
    }

    public void setColumn1(ColumnInterface column1) {
        m_column1 = column1;
    }

    public ColumnInterface getCoulmn2() {
        return m_coulmn2;
    }

    public void setCoulmn2(ColumnInterface coulmn2) {
        m_coulmn2 = coulmn2;
    }

    public Sign getSign() {
        return m_sign;
    }

    public void setSign(Sign sign) {
        m_sign = sign;
    }

}

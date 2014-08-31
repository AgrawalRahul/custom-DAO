package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Sign;

public class GenericBitCheckerFilter implements GenericFilter {

    private ColumnInterface m_column;
    private int             m_position;
    private Sign            m_sign;

    public GenericBitCheckerFilter(ColumnInterface column, int position, Sign sign) {
        m_column = column;
        m_position = position;
        m_sign = sign;
    }

    public ColumnInterface getColumn() {
        return m_column;
    }

    public void setColumn(ColumnInterface column) {
        m_column = column;
    }

    public int getPosition() {
        return m_position;
    }

    public void setPosition(int position) {
        m_position = position;
    }

    public Sign getSign() {
        return m_sign;
    }

    public void setSign(Sign sign) {
        m_sign = sign;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(" and ").append(this.getColumn().getColumnName(joinIdentifier)).append(" & ")
                .append(new Double(Math.pow(2, getPosition() - 1)).intValue()).append(" ").append(getSign().getSign())
                .append(" ").append(new Double(Math.pow(2, getPosition() - 1)).intValue());
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" & ")
                .append(new Double(Math.pow(2, getPosition() - 1)).intValue()).append(" ").append(getSign().getSign()).append(" ")
                .append(new Double(Math.pow(2, getPosition() - 1)).intValue());
    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        return index;
    }

}

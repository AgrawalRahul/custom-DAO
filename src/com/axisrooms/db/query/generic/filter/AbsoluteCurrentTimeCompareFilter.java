package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Operation;
import com.axisrooms.db.query.generic.Sign;

public class AbsoluteCurrentTimeCompareFilter implements GenericFilter {

    public ColumnInterface m_timeTypeColumn;
    private int            m_hours     = 0;
    private int            m_minutes   = 0;
    private int            m_seconds   = 0;
    private Sign           m_sign      = Sign.GREATER_EQUAL;
    private Operation      m_operation = Operation.SUBSTRACT;

    public AbsoluteCurrentTimeCompareFilter(ColumnInterface column) {
        m_timeTypeColumn = column;
    }

    public AbsoluteCurrentTimeCompareFilter(ColumnInterface column, int hour, int min, int sec) {
        m_timeTypeColumn = column;
        m_hours = hour;
        m_minutes = min;
        m_seconds = sec;
    }

    public AbsoluteCurrentTimeCompareFilter(ColumnInterface column, int hour, int min, int sec, Sign sign) {
        m_timeTypeColumn = column;
        m_hours = hour;
        m_minutes = min;
        m_seconds = sec;
        m_sign = sign;
    }

    public AbsoluteCurrentTimeCompareFilter(ColumnInterface column, int hour, int min, int sec, Sign sign,
            Operation operation) {
        m_timeTypeColumn = column;
        m_hours = hour;
        m_minutes = min;
        m_seconds = sec;
        m_sign = sign;
        m_operation = operation;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(" and " + this.getTimeTypeColumn().getColumnName(joinIdentifier))
                .append(" " + getSign().getSign() + " ").append("CURRENT_TIMESTAMP ")
                .append(this.getOperation().getOperation())
                .append(" time'" + getHours() + ":" + getMinutes() + ":" + getSeconds() + "'");
    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(" and " + this.getTimeTypeColumn().getColumnName(joinIdentifier))
                .append(" " + getSign().getSign() + " ").append("CURRENT_TIMESTAMP ")
                .append(this.getOperation().getOperation())
                .append(" time'" + getHours() + ":" + getMinutes() + ":" + getSeconds() + "'");
    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        return index;
    }

    public Operation getOperation() {
        return m_operation;
    }

    public void setOperation(Operation operation) {
        m_operation = operation;
    }

    public ColumnInterface getTimeTypeColumn() {
        return m_timeTypeColumn;
    }

    public void setTimeTypeColumn(ColumnInterface timeTypeColumn) {
        m_timeTypeColumn = timeTypeColumn;
    }

    public int getHours() {
        return m_hours;
    }

    public void setHours(int hours) {
        m_hours = hours;
    }

    public int getMinutes() {
        return m_minutes;
    }

    public void setMinutes(int minutes) {
        m_minutes = minutes;
    }

    public int getSeconds() {
        return m_seconds;
    }

    public void setSeconds(int seconds) {
        m_seconds = seconds;
    }

    public Sign getSign() {
        return m_sign;
    }

    public void setSign(Sign sign) {
        m_sign = sign;
    }

}

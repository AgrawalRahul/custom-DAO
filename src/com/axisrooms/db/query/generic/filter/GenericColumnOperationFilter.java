package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.Operation;
import com.axisrooms.db.query.generic.Sign;

public class GenericColumnOperationFilter implements GenericFilter {

    private ColumnInterface m_column;
    private ColumnInterface m_otherColumn;
    private long            m_data;
    private Sign            m_sign;
    private Operation       m_operation;

    public GenericColumnOperationFilter(ColumnInterface column, long data, Sign sign, Operation operation) {
        m_column = column;
        m_otherColumn = column;
        m_data = data;
        m_sign = sign;
        m_operation = operation;
    }

    public GenericColumnOperationFilter(ColumnInterface column, ColumnInterface otherColumn, long data, Sign sign,
            Operation operation) {
        m_column = column;
        m_otherColumn = otherColumn;
        m_data = data;
        m_sign = sign;
        m_operation = operation;
    }

    @Override
    public void appendQuery(StringBuilder sbuf, String joinIdentifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier) {
        sbuf.append(this.getColumn().getColumnName(joinIdentifier)).append(" ").append(getSign().getSign()).append(" ")
                .append(this.getOtherColumn().getColumnName(joinIdentifier)).append(" ")
                .append(getOperation().getOperation()).append(" ?");
    }

    @Override
    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception {
        psmt.setLong(index++, this.getData());
        return index;
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

    public Sign getSign() {
        return m_sign;
    }

    public void setSign(Sign sign) {
        m_sign = sign;
    }

    public Operation getOperation() {
        return m_operation;
    }

    public void setOperation(Operation operation) {
        m_operation = operation;
    }

    public ColumnInterface getOtherColumn() {
        return m_otherColumn;
    }

    public void setOtherColumn(ColumnInterface otherColumn) {
        m_otherColumn = otherColumn;
    }

}

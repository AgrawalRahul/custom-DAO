package com.axisrooms.db.query.generic;

public enum Operation {

    ADD("+"), SUBSTRACT("-");

    private String m_operation;

    private Operation(String operation) {
        m_operation = operation;
    }

    public String getOperation() {
        return m_operation;
    }

}

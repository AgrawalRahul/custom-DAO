package com.axisrooms.db.query.generic;

public enum Sign {

    EQUALS("="), GREATER(">"), LESS("<"), GREATER_EQUAL(">="), LESS_EQUAL("<="),NOT_EQUALS("<>");

    private String m_sign;

    private Sign(String sign) {
        m_sign = sign;
    }

    public String getSign() {
        return m_sign;
    }

}

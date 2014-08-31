package com.axisrooms.db.query.generic.filter;

import java.sql.PreparedStatement;

public interface GenericFilter {

    public void appendQuery(StringBuilder sbuf, String joinIdentifier);
    
    public void appendPreparedStatementString(StringBuilder sbuf, String joinIdentifier);

    public int appendPreparedStatementValue(PreparedStatement psmt, int index) throws Exception;

}

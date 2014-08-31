package com.axisrooms.db.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface ColumnInterface<T> {
    public String getColumnName();

    public String getColumnName(String joinIdentifier);

    public String getSelectorColumnName(String joinIdentifier);

    public String getDataSetName(String joinIdentifier);

    public void populateFromDataSet(T object, ResultSet rs, String joinIdentifier) throws Exception;

    public void setInPreparedStatement(T object, PreparedStatement pstmt, int index) throws Exception;
    

}

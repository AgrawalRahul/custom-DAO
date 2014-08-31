package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface SqlQuery extends SqlConstants {

    public String getTableName();

    public String getQueryString() throws Exception;

    /**
     * Prepared statement will be create with the query string provided by the
     * above function and will be passed to this function to set the parameters
     * of the prepared statement
     * 
     * @param pstmt: prepared statement not null set the parameters in this
     *            statement
     * @throws Exception
     */
    public void setQueryParameters(PreparedStatement pstmt) throws Exception;

    /**
     * This method will be called after executing the query with the result of
     * the query executed
     * 
     * @param rs : if the query returns a ResultSet then rs is passed else this
     *            is null
     * @param updateCount : if the query is a update/insert/delete query then
     *            updateCount is provided -1 is provided for select queries
     * @throws Exception
     */
    public void processResultSet(ResultSet rs, int updateCount) throws Exception;

    public List<SqlQuery> getChildQueries();

}

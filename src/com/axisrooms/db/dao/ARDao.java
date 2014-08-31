package com.axisrooms.db.dao;

import java.util.List;

import com.axisrooms.db.QueryManager;
import com.axisrooms.db.query.AbstractInsertQuery;
import com.axisrooms.db.query.AbstractUpdateQuery;
import com.axisrooms.db.query.BaapSearchQuery;
import com.axisrooms.db.query.SqlQuery;

public interface ARDao {

    public boolean executeQuery(QueryManager manager, SqlQuery query);

    public boolean executeQuery(SqlQuery query);

    public <E> List<E> fetchEntities(QueryManager manager, BaapSearchQuery<E> searchQuery);

    public <E> List<E> fetchEntities(BaapSearchQuery<E> searchQuery);

    public <E> E fetchEntity(QueryManager manager, BaapSearchQuery<E> searchQuery);

    public <E> E fetchEntity(BaapSearchQuery<E> searchQuery);

    public boolean insertEntity(QueryManager manager, AbstractInsertQuery insertQuery);

    public boolean insertEntity(AbstractInsertQuery insertQuery);

    public boolean updateEntity(AbstractUpdateQuery updateQuery);

    public long executeSequence(String sequenceName) throws Exception;

}

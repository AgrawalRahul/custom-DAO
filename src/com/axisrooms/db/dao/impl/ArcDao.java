package com.axisrooms.db.dao.impl;

import java.util.List;

import com.axisrooms.db.ArcQueryManager;
import com.axisrooms.db.query.BaapSearchQuery;
import com.axisrooms.db.query.SqlQuery;

public interface ArcDao {

    public boolean executeQuery(SqlQuery query);

    boolean executeQuery(ArcQueryManager manager, SqlQuery query);

    public <E> List<E> fetchEntities(ArcQueryManager manager, BaapSearchQuery<E> searchQuery);

    public <E> List<E> fetchEntities(BaapSearchQuery<E> searchQuery);

    public <E> E fetchEntity(ArcQueryManager manager, BaapSearchQuery<E> searchQuery);

    public <E> E fetchEntity(BaapSearchQuery<E> searchQuery);

}

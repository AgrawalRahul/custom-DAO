package com.axisrooms.db.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.axisrooms.db.ArcQueryManager;
import com.axisrooms.db.query.AbstractInsertQuery;
import com.axisrooms.db.query.BaapSearchQuery;
import com.axisrooms.db.query.SqlQuery;

public class ArcDaoImpl implements ArcDao {

    private static final Logger s_logger = Logger.getLogger(ARDaoImpl.class);

    @Override
    public boolean executeQuery(SqlQuery query) {
        return executeQuery(null, query);
    }

    @Override
    public boolean executeQuery(ArcQueryManager manager, SqlQuery query) {
        s_logger.debug("Executing SQL Query");
        if (query == null) {
            throw new NullPointerException("Query is null");
        }
        boolean transaction = false;
        if (manager == null) {
            transaction = true;
            manager = ArcQueryManager.getInstance();
        }
        boolean success = true;
        try {
            if (transaction) {
                manager.beginTransaction();
            }
            if (query instanceof AbstractInsertQuery) {
                AbstractInsertQuery tmp = (AbstractInsertQuery) query;
                if (tmp.getSequenceName() != null) {
                    tmp.setNextIdQuery(tmp.new NextIdQuery());
                    success = success && manager.execute(tmp.getNextIdQuery());
                }
                success = success && manager.execute(tmp);
            } else {
                success = success && manager.execute(query);
            }
            if (query.getChildQueries() != null) {
                for (SqlQuery childQuery : query.getChildQueries()) {
                    success = success && executeQuery(manager, childQuery);
                }
            }
        } catch (Exception e) {
            success = false;
            s_logger.error("Error executing insert query.", e);
        } finally {
            try {
                if (transaction) {
                    if (!success) {
                        manager.rollbackTransaction();
                    } else {
                        manager.endTransaction();
                    }
                }
            } catch (Exception e) {
                s_logger.error("Error finalizing insert query.", e);
            }
        }
        return success;
    }

    @Override
    public <E> List<E> fetchEntities(BaapSearchQuery<E> searchQuery) {
        return fetchEntities(null, searchQuery);
    }

    @Override
    public <E> E fetchEntity(ArcQueryManager manager, BaapSearchQuery<E> searchQuery) {
        if (searchQuery == null) {
            throw new NullPointerException("Search Query is null");
        }
        fetchEntities(manager, searchQuery);
        if (searchQuery.getResults() != null && searchQuery.getResults().size() > 1) {
            throw new RuntimeException("Internal Error: More than one Entites for search query: "
                    + searchQuery.getClass().getSimpleName());
        }
        if (searchQuery.getResults() == null || searchQuery.getResults().size() == 0) {
            return null;
        }
        return searchQuery.getResults().get(0);
    }

    @Override
    public <E> E fetchEntity(BaapSearchQuery<E> searchQuery) {
        return fetchEntity(null, searchQuery);
    }

    @Override
    public <E> List<E> fetchEntities(ArcQueryManager manager, BaapSearchQuery<E> searchQuery) {
        if (searchQuery == null) {
            throw new NullPointerException("Search Query is null");
        }
        if (manager == null) {
            manager = ArcQueryManager.getInstance();
        }
        List<E> results = null;
        try {
            manager.execute(searchQuery);
            results = searchQuery.getResults();
        } catch (Exception e) {
            s_logger.error("Error fetching entities for search query: " + searchQuery.getClass().getSimpleName(), e);
        }
        return results;
    }

}

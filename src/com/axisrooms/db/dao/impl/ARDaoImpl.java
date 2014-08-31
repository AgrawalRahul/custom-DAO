package com.axisrooms.db.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.axisrooms.db.QueryManager;
import com.axisrooms.db.dao.ARDao;
import com.axisrooms.db.query.AbstractInsertQuery;
import com.axisrooms.db.query.AbstractUpdateQuery;
import com.axisrooms.db.query.BaapSearchQuery;
import com.axisrooms.db.query.ExecuteSequenceQuery;
import com.axisrooms.db.query.SqlQuery;

public class ARDaoImpl implements ARDao {

    private static final Logger s_logger = Logger.getLogger(ARDaoImpl.class);

    @Override
    public boolean executeQuery(SqlQuery query) {
        return executeQuery(null, query);
    }

    @Override
    public boolean executeQuery(QueryManager manager, SqlQuery query) {
        s_logger.debug("Executing SQL Query");
        if (query == null) {
            throw new NullPointerException("Query is null");
        }
        boolean transaction = false;
        if (manager == null) {
            transaction = true;
            manager = QueryManager.getInstance();
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
    public long executeSequence(String sequenceName) throws Exception {
        ExecuteSequenceQuery sequenceQuery = new ExecuteSequenceQuery();
        sequenceQuery.setSequenceName(sequenceName);
        if (executeQuery(sequenceQuery)) {
            return sequenceQuery.getNextId();
        } else {
            throw new Exception("Unable to execute the sequence");
        }
    }

    @Override
    public boolean insertEntity(QueryManager manager, AbstractInsertQuery insertQuery) {
        if (insertQuery == null) {
            throw new NullPointerException("Insert Query is null");
        }
        return this.executeQuery(manager, insertQuery);
    }

    @Override
    public boolean insertEntity(AbstractInsertQuery insertQuery) {
        return this.insertEntity(null, insertQuery);
    }

    @Override
    public <E> List<E> fetchEntities(QueryManager manager, BaapSearchQuery<E> searchQuery) {
        if (searchQuery == null) {
            throw new NullPointerException("Search Query is null");
        }
        if (manager == null) {
            manager = QueryManager.getInstance();
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

    @Override
    public <E> List<E> fetchEntities(BaapSearchQuery<E> searchQuery) {
        return fetchEntities(null, searchQuery);
    }

    @Override
    public <E> E fetchEntity(QueryManager manager, BaapSearchQuery<E> searchQuery) {
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
    @Deprecated
    public boolean updateEntity(AbstractUpdateQuery updateQuery) {
        if (updateQuery == null) {
            throw new NullPointerException("Update Query is null");
        }
        QueryManager manager = QueryManager.getInstance();
        boolean transactionRequired = updateQuery.getNextUpdateQuery() != null ? true : false;
        AbstractUpdateQuery currentUpdateQuery = updateQuery;
        boolean success = true;
        try {
            if (transactionRequired) {
                manager.beginTransaction();
            }
            while (currentUpdateQuery != null) {
                success = success && manager.execute(currentUpdateQuery);
                currentUpdateQuery = currentUpdateQuery.getNextUpdateQuery();
            }
        } catch (Exception e) {
            success = false;
            s_logger.error("Error executing update query: " + currentUpdateQuery.getClass().getSimpleName(), e);
        } finally {
            try {
                if (transactionRequired) {
                    if (success) {
                        manager.endTransaction();
                    } else {
                        manager.rollbackTransaction();

                    }
                }
            } catch (Exception e) {
                s_logger.error("Error executing update query: " + currentUpdateQuery.getClass().getSimpleName());
            }
        }
        return success;
    }

}

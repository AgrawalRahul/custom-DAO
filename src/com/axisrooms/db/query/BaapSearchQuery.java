package com.axisrooms.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.filter.GenericFilter;

public class BaapSearchQuery<E> implements SqlQuery {

    private String                     m_tableName;

    private List<E>                    m_results;
    private List<ColumnInterface>      m_columns;

    private String                     m_joinIdentifier     = "";
    private List<BaapSearchQuery< ? >> m_childSearchQueries = null;
    private Class<E>                   m_instanceClass      = null;
    private EntityDBFactory            m_factory            = null;
    private List<GenericFilter>        m_genericFilters     = null;
    // Attributes required by child queries.
    private AssociationType            m_associationType    = null;
    private ColumnInterface            m_parentJoinColumn   = null;
    private ColumnInterface            m_joinColumn         = null;

    public BaapSearchQuery(String tableName, Class<E> instanceClass) {
        setTableName(tableName);
        setInstanceClass(instanceClass);
    }

    public BaapSearchQuery(String tableName, EntityDBFactory entityDBFactory) {
        setTableName(tableName);
        setFactory(entityDBFactory);
    }

    private boolean isChildQuery() {
        return getAssociationType() != null;
    }

    public String getParamString() {
        StringBuilder sb = new StringBuilder();
        boolean and = isChildQuery();
        for (ColumnInterface column : getColumns()) {
            if (!sb.toString().contains(column.getSelectorColumnName(getJoinIdentifier()))) {
                if (and) {
                    sb.append(",");
                }
                sb.append(column.getSelectorColumnName(getJoinIdentifier()));
                and = true;
            }
        }
        if (getChildSearchQueries() != null && getChildSearchQueries().size() > 0) {
            for (BaapSearchQuery< ? > childSearchQuery : getChildSearchQueries()) {
                sb.append(childSearchQuery.getParamString());
            }
        }
        return sb.toString();
    }

    public String getFromString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTableName() + " " + getJoinIdentifier());
        if (getChildSearchQueries() != null && getChildSearchQueries().size() > 0) {
            for (BaapSearchQuery< ? > childSearchQuery : getChildSearchQueries()) {
                if (childSearchQuery.getAssociationType() != AssociationType.SAME_TABLE) {
                    sb.append(childSearchQuery.getAssociationType().getSqlRelation())
                            .append(childSearchQuery.getFromString())
                            .append(childSearchQuery.getAssociationType().getSqlRelationConstraint(this,
                                    childSearchQuery));
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void setQueryParameters(PreparedStatement pstmt) throws Exception {
        return;
    }

    public Object getNewInstance(ResultSet rs) throws Exception {
        if (getInstanceClass() != null) {
            return getInstanceClass().newInstance();
        }
        if (getFactory() != null) {
            //TODO
//            return getFactory().getNewInstance(this, rs);
        }
        return null;
    }

    @Override
    public void processResultSet(ResultSet rs, int updateCount) throws Exception {
        while (rs.next()) {
            processResultSet(rs, null);
        }
    }

    @SuppressWarnings("unchecked")
    public void processResultSet(ResultSet rs, Object obj) throws Exception {
        Object instance = getNewInstance(rs);
        if (instance == null) {
            return;
        }
        if (obj != null) {
            // Check Inheritance.
            if (instance.getClass().equals(obj.getClass())) {
                // No need to create a new object. Just populate the extra
                // parameters of base Class. Use the passed object.
                instance = (E) obj;
            } else if (obj.getClass().getSuperclass() != Object.class
                    && obj.getClass().getSuperclass().isInstance(instance)) {
                // This is not the correct base class. This is used when say..
                // obj is supplier and instance is support.. both are parallel
                // classes.
                return;
            }
        }

        E result = (E) instance;
        for (ColumnInterface column : getColumns()) {
            column.populateFromDataSet(result, rs, getJoinIdentifier());
        }
        getResults().add(result);
        if (getChildSearchQueries() != null && getChildSearchQueries().size() > 0) {
            for (BaapSearchQuery< ? > childSearchQuery : getChildSearchQueries()) {
                childSearchQuery.processResultSet(rs, instance);
            }
        }
    }

    public String getWhereClauseString() {
        StringBuilder sb = new StringBuilder();
        if (this.getGenericFilters() != null) {
            for (GenericFilter filter : this.getGenericFilters()) {
                filter.appendQuery(sb, this.getJoinIdentifier());
            }
        }
        if (getChildSearchQueries() != null && getChildSearchQueries().size() > 0) {
            for (BaapSearchQuery< ? > childSearchQuery : this.getChildSearchQueries()) {
                sb.append(childSearchQuery.getWhereClauseString());
            }
        }
        return sb.toString();
    }

    @Override
    public String getQueryString() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(getParamString());
        sb.append(" from ");
        sb.append(getFromString());
        sb.append(" where true ");
        String whereClauseString = getWhereClauseString();
        // if (whereClauseString.trim().length() == 0) {
        // throw new Exception("Please Set Some parameters to search on!");
        // }
        sb.append(whereClauseString);
        return sb.toString();
    }

    public List<ColumnInterface> getColumns() {
        return m_columns;
    }

    public void setColumns(List<ColumnInterface> columns) {
        this.m_columns = columns;
    }

    public void setColumns(ColumnInterface[] columns) {
        this.m_columns = Arrays.asList(columns);
    }

    public ColumnInterface getParentJoinColumn() {
        return m_parentJoinColumn;
    }

    private void setParentJoinColumn(ColumnInterface parentJoinColumn) {
        this.m_parentJoinColumn = parentJoinColumn;
    }

    public ColumnInterface getJoinColumn() {
        return m_joinColumn;
    }

    private void setJoinColumn(ColumnInterface joinColumn) {
        this.m_joinColumn = joinColumn;
    }

    public String getJoinIdentifier() {
        if (m_joinIdentifier == null) {
            m_joinIdentifier = "";
        }
        return m_joinIdentifier;
    }

    public void setJoinIdentifier(String joinIdentifier) {
        this.m_joinIdentifier = joinIdentifier;
    }

    public List<BaapSearchQuery< ? >> getChildSearchQueries() {
        if (m_childSearchQueries == null) {
            m_childSearchQueries = new ArrayList<BaapSearchQuery< ? >>();
        }
        return m_childSearchQueries;
    }

    public void addChildSearchQuery(BaapSearchQuery< ? > childSearchQuery, AssociationType associationType,
            ColumnInterface joinColumn, ColumnInterface parentJoinColumn) {
        childSearchQuery.setAssociationType(associationType);
        if (associationType == AssociationType.SAME_TABLE) {
            childSearchQuery.setJoinIdentifier(getJoinIdentifier());
        }
        childSearchQuery.setJoinColumn(joinColumn);
        childSearchQuery.setParentJoinColumn(parentJoinColumn);

        getChildSearchQueries().add(childSearchQuery);
    }

    @Override
    public String getTableName() {
        return m_tableName;
    }

    public List<E> getResults() {
        if (m_results == null) {
            m_results = new ArrayList<E>();
        }
        return m_results;
    }

    public void setResults(List<E> results) {
        this.m_results = results;
    }

    public AssociationType getAssociationType() {
        return m_associationType;
    }

    private void setAssociationType(AssociationType joinType) {
        this.m_associationType = joinType;
    }

    public EntityDBFactory getFactory() {
        return m_factory;
    }

    public void setFactory(EntityDBFactory factory) {
        m_factory = factory;
    }

    public Class<E> getInstanceClass() {
        return m_instanceClass;
    }

    public void setInstanceClass(Class<E> class1) {
        m_instanceClass = class1;
    }

    public void setTableName(String tableName) {
        m_tableName = tableName;
    }

    public List<GenericFilter> getGenericFilters() {
        if (m_genericFilters == null) {
            m_genericFilters = new ArrayList<GenericFilter>();
        }
        return m_genericFilters;
    }

    public void setGenericFilters(List<GenericFilter> genericFilters) {
        m_genericFilters = genericFilters;
    }

    public void addFilter(GenericFilter filter) {
        this.getGenericFilters().add(filter);
    }

    @Override
    public List<SqlQuery> getChildQueries() {
        return null;
    }
}

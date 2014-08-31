package com.axisrooms.db.query;

import java.util.ArrayList;
import java.util.List;

import com.axisrooms.db.column.ColumnInterface;
import com.axisrooms.db.query.generic.filter.GenericFilter;

public class GenericSearchQueryOld {

    // User multiple child parent filter
    // Inner join define by type
    // Use prepared statement

    private String                   m_tableName;
    private String                   m_joinIdentifier;

    private ColumnInterface[]        selectColumns  = null;

    private boolean                  m_isJoin       = false;
    private ColumnInterface          m_joinColumn   = null;
    private ColumnInterface          m_parentColumn = null;
    private List<GenericFilter>      m_genericFilters;

    private GenericSearchQueryOld       m_childQuery;

    private List<GenericSearchQueryOld> m_childQueries = null;

    public GenericSearchQueryOld() {

    }

    public GenericSearchQueryOld(String tableName, String joinIdentifier) {
        m_tableName = tableName;
        m_joinIdentifier = joinIdentifier;
    }

    
    public String getFromString() {
        StringBuilder sb = new StringBuilder();
        if (getChildQueries() != null && getChildQueries().size() > 0) {
            boolean firstTime = true;
            for (GenericSearchQueryOld childSearchQuery : getChildQueries()) {
                if (firstTime) {
                    sb.append(getTableName()).append(" ").append(getJoinIdentifier());
                    firstTime = false;
                }
                sb.append(" inner join ").append(childSearchQuery.getFromString());
                sb.append(" on (").append(childSearchQuery.getParentColumn().getColumnName(getJoinIdentifier()))
                        .append("=").append(
                                childSearchQuery.getJoinColumn().getColumnName(childSearchQuery.getJoinIdentifier()))
                        .append(") ");
            }
        } else {
            sb.append(getTableName() + " " + getJoinIdentifier());
        }
        return sb.toString();
    }

    public ColumnInterface getJoinColumn() {
        return m_joinColumn;
    }

    @Deprecated
    public String getJoinColumnName() {
        return m_joinColumn.getColumnName(this.getJoinIdentifier());
    }

    public String getJoinIdentifier() {
        return m_joinIdentifier;
    }

    public String getParamString() {
        StringBuilder sb = new StringBuilder();
        boolean and = isJoin();
        for (ColumnInterface column : getSelectColumns()) {
            if (and) {
                sb.append(",");
            }
            sb.append(column.getSelectorColumnName(getJoinIdentifier()));
            and = true;
        }
        if (getChildQueries() != null && getChildQueries().size() > 0) {
            for (GenericSearchQueryOld childQuery : getChildQueries()) {
                childQuery.setJoin(true);
                sb.append(childQuery.getParamString());
            }
        }
        return sb.toString();
    }

    public ColumnInterface getParentColumn() {
        return m_parentColumn;
    }

    public String getQueryString() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(getParamString());
        sb.append(" from ");
        sb.append(this.getFromString());
        sb.append(" where true ");
        String whereClauseString = getWhereClauseString();
        sb.append(whereClauseString);
        return sb.toString();
    }

    
    public String getTableName() {
        return m_tableName;
    }

    
    public String getWhereClauseString() {
        StringBuilder sb = new StringBuilder();
        if (this.getGenericFilters() != null) {
            for (GenericFilter filter : this.getGenericFilters()) {
                filter.appendQuery(sb, this.getJoinIdentifier());
            }
        }
        if (this.getChildQueries() != null && this.getChildQueries().size() > 0) {
            for (GenericSearchQueryOld childQuery : this.getChildQueries()) {
                sb.append(childQuery.getWhereClauseString());
            }
        }
        return sb.toString();
    }

    
    public boolean isJoin() {
        return m_isJoin;
    }

    
    public void setJoin(boolean isJoin) {
        m_isJoin = isJoin;
    }

    
    public void setJoinIdentifier(String joinIdentifier) {
        m_joinIdentifier = joinIdentifier;
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

    public ColumnInterface[] getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(ColumnInterface[] selectColumns) {
        this.selectColumns = selectColumns;
    }

    public GenericSearchQueryOld getChildQuery() {
        return m_childQuery;
    }

    public void setChildQuery(GenericSearchQueryOld childQuery) {
        setChildQueries(new ArrayList<GenericSearchQueryOld>());
        getChildQueries().add(childQuery);
    }

    public void setParentColumn(ColumnInterface parentColumn) {
        m_parentColumn = parentColumn;
    }

    public void addFilter(GenericFilter filter) {
        this.getGenericFilters().add(filter);
    }

    public void setJoinColumn(ColumnInterface joinColumn) {
        m_joinColumn = joinColumn;
    }

    public void setTableName(String tableName) {
        m_tableName = tableName;
    }

    public void addChildQuery(GenericSearchQueryOld userSearchQuery) {
        if (getChildQueries() == null) {
            setChildQueries(new ArrayList<GenericSearchQueryOld>());
        }
        getChildQueries().add(userSearchQuery);
    }

    public List<GenericSearchQueryOld> getChildQueries() {
        return m_childQueries;
    }

    public void setChildQueries(List<GenericSearchQueryOld> childQueries) {
        m_childQueries = childQueries;
    }

}

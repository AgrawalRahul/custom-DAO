package com.axisrooms.db.query;

public enum AssociationType implements SqlConstants {
    SAME_TABLE {
        public String getSqlRelation() {
            return EMPTY;
        };

        @Override
        public String getSqlRelationConstraint(BaapSearchQuery< ? > parentSearchQuery,
                BaapSearchQuery< ? > childSearchQuery) {
            return EMPTY;
        }
    },
    INNER_JOIN {
        public String getSqlRelation() {
            return " inner join ";
        };

        @Override
        public String getSqlRelationConstraint(BaapSearchQuery< ? > parentSearchQuery,
                BaapSearchQuery< ? > childSearchQuery) {
            return " on(" + childSearchQuery.getParentJoinColumn().getColumnName(parentSearchQuery.getJoinIdentifier())
                    + EQUAL + childSearchQuery.getJoinColumn().getColumnName(childSearchQuery.getJoinIdentifier())
                    + ")";
        }
    },
    LEFT_OUTER_JOIN {
        public String getSqlRelation() {
            return " left outer join ";
        };

        @Override
        public String getSqlRelationConstraint(BaapSearchQuery< ? > parentSearchQuery,
                BaapSearchQuery< ? > childSearchQuery) {
            return " on(" + childSearchQuery.getParentJoinColumn().getColumnName(parentSearchQuery.getJoinIdentifier())
                    + EQUAL + childSearchQuery.getJoinColumn().getColumnName(childSearchQuery.getJoinIdentifier())
                    + ") ";
        }
    };

    private AssociationType() {
    }

    public abstract String getSqlRelation();

    public abstract String getSqlRelationConstraint(BaapSearchQuery< ? > parentSearchQuery,
            BaapSearchQuery< ? > childSearchQuery);

}

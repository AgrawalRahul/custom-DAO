package com.axisrooms.db.query;

public enum InheritanceType {
    TABLE_PER_HIERARCHY("inner join"), TABLE_PER_SUBCLASS("left outer join");

    private String syntax;

    private InheritanceType(String syntax) {
        setSyntax(syntax);
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }
}

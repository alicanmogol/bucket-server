package com.fererlab.dto;

/**
 * acm 11/13/12 8:19 PM
 */
public enum ParamRelation {

    EQ("equal"),
    NE("not equal"),
    IEQ("case insensitive equal"),
    BETWEEN("between"),
    GT("greater than"),
    GE("greater than or equal to"),
    LT("less than"),
    LE("less than or equal to");

    private String name;

    ParamRelation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal();
    }
}
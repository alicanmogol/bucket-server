package com.fererlab.dto;

/**
 * acm 10/15/12 4:45 PM
 */
public enum SessionKeys {

    USERNAME("USERNAME"),
    PASSWORD("PASSWORD"),
    AUTHENTICATION_TYPE("AUTHENTICATION_TYPE"),
    SESSION_STORED_AT("SESSION_STORED_AT"),
    SESSION_ID("SESSION_ID"),
    IS_LOGGED("IS_LOGGED"),
    COOKIE("Cookie");

    private final String value;

    SessionKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

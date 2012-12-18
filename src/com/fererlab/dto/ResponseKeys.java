package com.fererlab.dto;

/**
 * acm 10/15/12 4:45 PM
 */
public enum ResponseKeys {

    STATUS("status"),
    MESSAGE("message"),
    EXPIRES("Expires"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SERVER("Server");


    private final String value;

    ResponseKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

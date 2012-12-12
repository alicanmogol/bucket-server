package com.fererlab.dto;

/**
 * acm 10/15/12 4:44 PM
 */
public enum RequestKeys {

    URL("URL"),
    PROTOCOL("PROTOCOL"),
    IP("IP"),
    PORT("PORT"),
    URI("URI"),
    QUERY_STRING("QUERY_STRING"),
    REQUEST_METHOD("REQUEST_METHOD"),
    HEADER_NAMES("HEADER_NAMES");

    private final String value;

    RequestKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

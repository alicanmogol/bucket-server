package com.fererlab.server;

/**
 * acm | 12/11/12 7:26 PM
 */
public enum PropertyKeys {

    LISTEN_PORTS("listen.ports"), MAXIMUM_THREAD_COUNT("maximum.thread.count"), CONFIG_FILE("config");

    private final String value;

    PropertyKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

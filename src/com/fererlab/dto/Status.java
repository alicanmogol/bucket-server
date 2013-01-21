package com.fererlab.dto;

import java.io.Serializable;

/**
 * acm 10/15/12 4:27 PM
 */
public class Status implements Serializable {

    public final static Status STATUS_OK = new Status(200, "OK");
    public final static Status STATUS_FORBIDDEN = new Status(403, "FORBIDDEN");
    public final static Status STATUS_NOT_FOUND = new Status(403, "NOT FOUND");
    public final static Status STATUS_SERVICE_UNAVAILABLE = new Status(503, "SERVICE UNAVAILABLE");

    private final int status;
    private final String message;

    public Status(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

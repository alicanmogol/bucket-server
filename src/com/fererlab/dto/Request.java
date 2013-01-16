package com.fererlab.dto;

import java.io.Serializable;

/**
 * acm 10/15/12 4:16 PM
 */
public class Request implements Serializable {

    private ParamMap<String, Param<String, Object>> headers;
    private ParamMap<String, Param<String, Object>> params;
    private Session<String, String> session;

    public Request() {
    }

    public Request(ParamMap<String, Param<String, Object>> params, ParamMap<String, Param<String, Object>> headers, Session<String, String> session) {
        this.session = session;
        this.params = params;
        this.headers = headers;
    }

    public Session<String, String> getSession() {
        return session;
    }

    public ParamMap<String, Param<String, Object>> getHeaders() {
        return headers;
    }

    public ParamMap<String, Param<String, Object>> getParams() {
        return params;
    }

}

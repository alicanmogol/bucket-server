package com.fererlab.dto;

import java.io.Serializable;

/**
 * acm 10/15/12 4:16 PM
 */
public class Request implements Serializable {

    private Session session;

    private ParamMap<String, Param<String, Object>> params = new ParamMap<>();

    public Request() {
    }

    public Request(ParamMap<String, Param<String, Object>> params, Session session) {
        this.session = session;
        this.params = params;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public ParamMap<String, Param<String, Object>> getParams() {
        return params;
    }
}

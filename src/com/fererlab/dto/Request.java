package com.fererlab.dto;

import java.io.Serializable;
import java.util.List;

/**
 * acm 10/15/12 4:16 PM
 */
public class Request implements Serializable {

    private Session session;

    private ParamMap<String, String> params = new ParamMap<>();

    public Request(List<Param<String, String>> params, Session session) {
        this.session = session;
        this.params.addParams(params);
    }

    public Session getSession() {
        return session;
    }

    public ParamMap<String, String> getParams() {
        return params;
    }
}

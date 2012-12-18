package com.fererlab.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * acm 10/15/12 4:16 PM
 */
public class Response implements Serializable {

    private ParamMap<String, Param<String, Object>> params = new ParamMap<>();
    private Session session;
    private Status status;
    private String content;

    public Response(ParamMap<String, Param<String, Object>> params, Session session, Status status, String content) {
        this.params = params;
        this.session = session;
        this.status = status;
        this.content = content;
    }

    public ParamMap<String, Param<String, Object>> getParams() {
        return params;
    }

    public Session getSession() {
        return session;
    }

    public Status getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public byte[] write() {
        StringBuilder sb = new StringBuilder();
        // add response code
        sb.append(params.get(ResponseKeys.PROTOCOL.getValue()).getValue());
        sb.append(params.get(ResponseKeys.STATUS.getValue()).getValue());
        sb.append(" ");
        sb.append(params.get(ResponseKeys.MESSAGE.getValue()).getValue());
        sb.append("\n");


        // add date
        sb.append("Date: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss z");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append("\n");

        // set cookie
        sb.append(getSession().toCookie());

        // add all the params
        for (Param<String, Object> param : params.getParamList()) {
            sb.append(param.getKey());
            sb.append(": ");
            sb.append(param.getValue());
            sb.append("\n");
        }

        // end headers
        sb.append("\r\n");


        // add content
        sb.append(content);

        // append the delimiters
        sb.append("\n\r\n\r");

        try {
            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return sb.toString().getBytes();
        }

    }
}

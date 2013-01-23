package com.fererlab.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * acm 10/15/12
 */
public class Response implements Serializable {

    private ParamMap<String, Param<String, Object>> headers;
    private Session<String, String> session;
    private Status status;
    private String content;

    public Response(ParamMap<String, Param<String, Object>> headers, Session<String, String> session, Status status, String content) {
        this.headers = headers;
        this.session = session;
        this.status = status;
        this.content = content;
    }

    public ParamMap<String, Param<String, Object>> getHeaders() {
        return headers;
    }

    public void setHeaders(ParamMap<String, Param<String, Object>> headers) {
        this.headers = headers;
    }

    public Session<String, String> getSession() {
        return session;
    }

    public void setSession(Session<String, String> session) {
        this.session = session;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", session=" + session +
                ", status=" + status +
                ", content='" + content + '\'' +
                '}';
    }

    public byte[] write() {
        StringBuilder sb = new StringBuilder();
        // add response code
        sb.append(headers.get(ResponseKeys.PROTOCOL.getValue()).getValue());
        sb.append(" ");
        sb.append(status.getStatus());
        sb.append(" ");
        sb.append(status.getMessage());
        sb.append("\n");


        // add date
        sb.append("Date: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss z");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append("\n");

        // set cookie
        sb.append(getSession().toCookie());

        // add all the headers
        for (Param<String, Object> param : headers.getParamList()) {
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

    /*
    static create response method
     */

    public static Response create(final Request request, String content, Status status) {
        return new Response(
                new ParamMap<String, Param<String, Object>>(),
                request.getSession(),
                status,
                content
        );
    }

}

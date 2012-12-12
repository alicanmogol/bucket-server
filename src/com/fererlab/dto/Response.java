package com.fererlab.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * acm 10/15/12 4:16 PM
 */
public class Response implements Serializable {

    private List<Param> params;
    private Session session;
    private Status status;
    private String content;

    public Response(List<Param> params, Session session, Status status, String content) {
        this.params = Collections.unmodifiableList(params);
        this.session = session;
        this.status = status;
        this.content = content;
    }

    public List<Param> getParams() {
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
        sb.append(status.getStatus());
        sb.append(" ");
        sb.append(status.getMessage());
        sb.append("\n");

        sb.append("Date: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss z");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append("\n");

        sb.append("Expires:  -1\n");
        sb.append("Cache-Control:  private, max-age=0\n");
        sb.append("Content-Type:  text/html; charset=UTF-8\n");
        sb.append("Set-Cookie:  ");
        sb.append(getSession().toCookie());
        sb.append("\n");

        sb.append("Transfer-Encoding:  chunked\n");

        sb.append("Server:  bucket\n");

        sb.append(content);

        sb.append("\n\r\n\r");

        try {
            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return sb.toString().getBytes();
        }
    }
}

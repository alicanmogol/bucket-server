package com.fererlab.dto;

import sun.security.pkcs.EncodingException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * acm 10/15/12
 */
public class Response implements Serializable {

    private ParamMap<String, Param<String, Object>> headers;
    private Session session;
    private Status status;
    private String content = null;
    private byte[] contentChar = null;

    public Response(ParamMap<String, Param<String, Object>> headers, Session session, Status status, byte[] contentChar) {
        this.headers = headers;
        this.session = session;
        this.status = status;
        this.setContentChar(contentChar);
    }

    public Response(ParamMap<String, Param<String, Object>> headers, Session session, Status status, String content) {
        this.headers = headers;
        this.session = session;
        this.status = status;
        this.setContent(content);
    }

    public ParamMap<String, Param<String, Object>> getHeaders() {
        return headers;
    }

    public void setHeaders(ParamMap<String, Param<String, Object>> headers) {
        this.headers = headers;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContent() {
        if (content != null) {
            return content;
        } else if (contentChar != null) {
            return new String(contentChar);
        }
        return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getContentChar() {
        return contentChar;
    }

    public void setContentChar(byte[] contentChar) {
        this.contentChar = contentChar;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", session=" + session +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", contentChar='" + Arrays.toString(contentChar) + '\'' +
                '}';
    }

    public void write(OutputStream outputStream) {
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

        // add contentChar if not add content
        if (contentChar != null) {
            try {
                // write the headers
                try {
                    outputStream.write(sb.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    outputStream.write(sb.toString().getBytes());
                }

                //write the file content
                outputStream.write(getContentChar());

                // write the delimiters
                outputStream.write("\n\r\n\r".getBytes());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            // append the content
            sb.append(getContent());

            // append the delimiters
            sb.append("\n\r\n\r");

            // write content to output stream
            try {
                try {
                    outputStream.write(sb.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    outputStream.write(sb.toString().getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                content == null ? "" : content
        );
    }

    public static Response internalServerError(Request request, Exception e) {
        StringBuilder exception = new StringBuilder();
        exception.append("\n<h3>\n");
        exception.append(e.getClass().getName());
        exception.append(": ");
        exception.append(e.getMessage());
        exception.append("\n</h3><h5>\n");
        for (StackTraceElement element : e.getStackTrace()) {
            exception.append(element.getClassName()).append(".").append(element.getMethodName()).append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")").append("\n<br/>\n");
        }
        e.printStackTrace();
        return Response.create(request, "<h1>" + Status.STATUS_INTERNAL_SERVER_ERROR.getMessage() + "</h1>" + exception + "</h5>", Status.STATUS_INTERNAL_SERVER_ERROR);
    }
}

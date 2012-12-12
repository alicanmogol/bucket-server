package com.fererlab.server;

import com.fererlab.dto.Request;
import com.fererlab.dto.Response;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

/**
 * acm | 12/11/12 10:57 PM
 */
public class Connection {

    private String id;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String rawRequest;
    private Socket socket;
    private Request request;
    private Response response;

    public Connection() {
        id = new Date().getTime() + "|" + new Random().nextDouble();
    }

    public String getId() {
        return id;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

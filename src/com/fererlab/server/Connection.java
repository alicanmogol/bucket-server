package com.fererlab.server;

import com.fererlab.app.ApplicationDescriptionHandler;
import com.fererlab.dto.Request;
import com.fererlab.dto.Response;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * acm | 12/11/12
 */
public class Connection {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String rawRequest;
    private Request request;
    private Response response;
    private ApplicationDescriptionHandler applicationDescriptionHandler;

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

    public void setApplicationDescriptionHandler(ApplicationDescriptionHandler applicationDescriptionHandler) {
        this.applicationDescriptionHandler = applicationDescriptionHandler;
    }

    public ApplicationDescriptionHandler getApplicationDescriptionHandler() {
        return applicationDescriptionHandler;
    }
}

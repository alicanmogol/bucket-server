package com.fererlab.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * acm | 12/18/12 9:32 AM
 */
public class BucketHttpServer implements HttpHandler {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new BucketHttpServer());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "Welcome Real's HowTo test page";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

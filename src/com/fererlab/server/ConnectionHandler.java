package com.fererlab.server;

import com.fererlab.app.ApplicationHandler;
import com.fererlab.dto.Param;
import com.fererlab.dto.Request;
import com.fererlab.dto.Response;
import com.fererlab.dto.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * acm | 12/11/12 10:52 PM
 */
public class ConnectionHandler implements Runnable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final Connection connection;

    public ConnectionHandler(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            log("sleeping");
            Thread.sleep(10000);
            log("sleeping done");

            // first read the request and set it to connection's raw request
            readRawRequest();

            // parse the request
            parseRequest();

            // run the application
            runApplication();

            // send the response back
            sendResponseBack();

        } catch (Exception e) {
            if (connection.getOutputStream() != null) {
                try {
                    connection.getOutputStream().write("SOCKET_EXCEPTION".getBytes());
                } catch (IOException ioe) {
                }
            }
            log("exception occurred: " + e);
            e.printStackTrace();
        } finally {
            if (connection.getInputStream() != null) {
                try {
                    connection.getInputStream().close();
                } catch (IOException ioe) {
                }
            }
            if (connection.getOutputStream() != null) {
                try {
                    connection.getOutputStream().close();
                } catch (IOException ioe) {
                }
            }
            if (connection.getSocket() != null) {
                try {
                    connection.getSocket().close();
                } catch (IOException ioe) {
                }
            }
            connection.setSocket(null);
            connection.setInputStream(null);
            connection.setOutputStream(null);
        }
        log("this ConnectionHandler shutdown");
    }

    private void readRawRequest() throws IOException {
        log("start reading the input stream");
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = connection.getInputStream().read()) != -1) {
            char currentChar = (char) ch;
            sb.append(currentChar);
            if (currentChar == '\n'
                    && sb.length() >= 4
                    && (sb.toString().substring(sb.toString().length() - 4, sb.toString().length()).equals("\r\n\r\n"))
                    ) {
                break;
            }
        }
        connection.setRawRequest(sb.toString());
        log("read request done: " + connection.getRawRequest());
    }

    private void parseRequest() {
        connection.setRequest(
                new Request(Arrays.<Param<String, String>>asList(), new Session())
        );
    }

    private void runApplication() {
        ApplicationHandler applicationHandler = new ApplicationHandler();
        Response response = applicationHandler.runApplication(connection.getRequest());
        connection.setResponse(response);
    }

    private void sendResponseBack() throws IOException {
        connection.getOutputStream().write(
                connection.getResponse().write()
        );
        connection.getOutputStream().close();
    }

    private void log(String log) {
        logger.info(log);
    }

}

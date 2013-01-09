package com.fererlab.server;

import com.fererlab.app.ApplicationHandler;
import com.fererlab.dto.*;

import java.io.IOException;
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
            log("ConnectionHandler run");

            // first read the request and set it to connection's raw request
            readRawRequest();

            // parse the request
            parseRequest();

            // run the application
            runApplication();

            // add default response headers
            addResponseHeaders();

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

            log("ConnectionHandler shutdown");
        }
    }

    private void readRawRequest() throws IOException {
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
    }

    private void parseRequest() {

        // create headers and params maps
        ParamMap<String, Param<String, Object>> headers = new ParamMap<>();
        ParamMap<String, Param<String, Object>> params = new ParamMap<>();

        /*
        // raw request should be something like below;

        GET /some/uri/with?multiple=params&like=this HTTP/1.1
        User-Agent: Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4
        Cookie: sessionval=ef0382f877cd6743c53e78f2bc3077bc; lastvisit=1355751890; lastactivity=0

        */
        String[] requestRows = connection.getRawRequest().split("\n");

        boolean requestUriFound = false;
        for (String requestRow : requestRows) {

            while (requestRow.endsWith("\r") || requestRow.endsWith("\n")) {
                requestRow = requestRow.substring(0, requestRow.length() - 1);
            }

            if (requestRow.isEmpty()) {
                continue;
            }

            String[] ifContainsMethodSecondPartIsUri;
            if (!requestUriFound) {
                ifContainsMethodSecondPartIsUri = requestRow.split("(^GET )|(^POST )|(^PUT )|(^DELETE )", 2);
            } else {
                ifContainsMethodSecondPartIsUri = null;
            }
            // this line is the request URI with http request method
            if (ifContainsMethodSecondPartIsUri != null && ifContainsMethodSecondPartIsUri.length > 1) {
                String[] methodUriPair = requestRow.split(" ", 2);
                requestUriFound = true;

                // add request method
                params.addParam(new Param<String, Object>(RequestKeys.REQUEST_METHOD.getValue(), methodUriPair[0]));

                // add request URI
                String[] uri = methodUriPair[1].split("HTTP");
                params.addParam(new Param<String, Object>(RequestKeys.URI.getValue(), uri[0].trim()));

                // add protocol which is HTTP
                params.addParam(new Param<String, Object>(RequestKeys.PROTOCOL.getValue(), "HTTP" + uri[1]));

                // set protocol like "HTTP/1.1"
                if (methodUriPair.length > 2) {
                    params.addParam(new Param<String, Object>(RequestKeys.PROTOCOL.getValue(), methodUriPair[2].trim()));
                }

                // find and set request method and uri
                String[] uriAndParams = uri[0].split("\\?", 2);

                // add params
                if (uriAndParams.length > 1) {
                    String[] requestParams = uriAndParams[1].split("&");
                    // requestParams    a=1&b<2&c>3&d=4&e!=5&5<f<7&g<=7&h>=8
                    for (String paramKeyValue : requestParams) {
                        String[] paramArr = null;

                        paramArr = paramKeyValue.split("(<=)|(%3C=)", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.LE));
                            continue;
                        }

                        paramArr = paramKeyValue.split("(>=)|(%3E=)", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.GE));
                            continue;
                        }

                        paramArr = paramKeyValue.split("!=", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.NE));
                            continue;
                        }

                        paramArr = paramKeyValue.split("=", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.EQ));
                            continue;
                        }

                        paramArr = paramKeyValue.split("((<)|(%3C))*((<)|(%3C))");
                        if (paramArr.length == 3) {
                            params.addParam(new Param<String, Object>(paramArr[1], paramArr[0], paramArr[2], ParamRelation.BETWEEN));
                            continue;
                        }

                        paramArr = paramKeyValue.split("(<)|(%3C)", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.LT));
                            continue;
                        }

                        paramArr = paramKeyValue.split("(>)|(%3E)", 2);
                        if (paramArr.length == 2) {
                            params.addParam(new Param<String, Object>(paramArr[0], paramArr[1], ParamRelation.GT));
                        }

                    }

                } else {
                    // no params passed, this is a URL like; /some/url/67/
                    // there won't be any parameters for request
                    // do nothing
                }

            }

            // these lines are the headers
            else {
                String[] keyValuePair = requestRow.split(":", 2);
                headers.addParam(new Param<String, Object>(keyValuePair[0], keyValuePair[1].trim()));
            }

        }

        // create, prepare and set the session to request
        Session<String, String> session = new Session<>();
        if (params.containsKey(SessionKeys.COOKIE.getValue())) {
            // Cookie: datr=c4zPUICqj0F-m2asLv74xo8B; reg_ext_ref=https%3A%2F%2Fwww.google.com%2F; reg_fb_gate=https%3A%2F%2Fwww.facebook.com%2Fmumtaz.khan.311056; reg_fb_ref=https%3A%2F%2Fwww.facebook.com%2F; wd=1366x363
            String[] cookieKeyValuePairs = params.get(SessionKeys.COOKIE.getValue()).getKey().split(";");
            for (String cookieKeyValuePair : cookieKeyValuePairs) {
                String[] keyValuePair = cookieKeyValuePair.trim().split("=", 2);
                if (keyValuePair.length == 2) {
                    // put this key and value pair to session
                    session.put(keyValuePair[0], keyValuePair[1]);
                } else {
                    // cookie values are key value pairs and they are represented like "key1=value1;key2=value;"
                    // in this case the keyValuePair does not contain 2 entries separated by "="
                    // will insert the keyValuePair as it is as a key
                    session.put(keyValuePair[0], "");
                }
            }
        } else {
            // request does not have any param with key "Cookie"
            // do nothing
        }

        // set request to connection object
        connection.setRequest(new Request(params, headers, session));

    }

    private void runApplication() {
        ApplicationHandler applicationHandler = new ApplicationHandler();
        Response response = applicationHandler.runApplication(connection.getRequest());
        connection.setResponse(response);
    }

    private void addResponseHeaders() {
        connection.getResponse().getParams().addParam(new Param<>(ResponseKeys.PROTOCOL.getValue(), connection.getRequest().getParams().get(RequestKeys.PROTOCOL.getValue()).getValue()));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.STATUS.getValue(), "" + Status.STATUS_SUCCESS.getStatus()));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.MESSAGE.getValue(), Status.STATUS_SUCCESS.getMessage()));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.EXPIRES.getValue(), "-1"));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.CACHE_CONTROL.getValue(), "Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0, private, max-age=0"));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.SERVER.getValue(), "bucket"));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.CONTENT_TYPE.getValue(), "text/html; charset=UTF-8"));
        connection.getResponse().getParams().addParam(new Param<String, Object>(ResponseKeys.CONTENT_LENGTH.getValue(), connection.getResponse().getContent().length() + 4)); // 4 is the number of the delimiter chars; \n\r\n\r
    }

    private void sendResponseBack() throws IOException {
        connection.getOutputStream().write(
                connection.getResponse().write()
        );
        connection.getOutputStream().flush();
    }

    private void log(String log) {
        logger.info("[" + Thread.currentThread().getId() + "] " + log);
    }

}

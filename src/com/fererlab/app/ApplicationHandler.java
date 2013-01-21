package com.fererlab.app;

import com.fererlab.dto.*;

import java.util.logging.Logger;

/**
 * acm | 12/12/12
 */
public class ApplicationHandler {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public Response runApplication(Request request) {
        try {
            if (request.getParams().containsKey(RequestKeys.URI.getValue())) {
                String uri = "";
                String hostName = (String) request.getHeaders().get(RequestKeys.HOST_NAME.getValue()).getValue();
                String hostPort = (String) request.getHeaders().get(RequestKeys.HOST_PORT.getValue()).getValue();
                String[] uriParts = ((String) request.getParams().get(RequestKeys.URI.getValue()).getValue()).split("/");
                if (uriParts.length > 1) {
                    uri = "/" + uriParts[1];
                }
                if (hostPort.equals("80") || hostPort.equals("443")) {
                    hostPort = "";
                } else {
                    hostPort = ":" + hostPort;
                }
                log("found request: " + hostName + hostPort + uri);
                Application application = findApplicationFromURI(hostName, hostPort, uri);
                if (application != null) {
                    log("found application for this uri: " + uri + ", will run application");
                    return application.runApplication(request);
                } else {
                    log("no application assigned for this URI: " + uri + ", no application to run");
                }
            } else {
                log("request does not have any param named 'URI', no application to run");
            }
            return new Response(new ParamMap<>(), new Session<String, String>(), Status.STATUS_NOT_FOUND, "");
        } catch (Exception e) {
            return new Response(new ParamMap<>(), new Session<String, String>(), Status.STATUS_SERVICE_UNAVAILABLE, "");
        }
    }

    private Application findApplicationFromURI(String hostname, String port, String uri) {

        //      sample-subdomain.*/sample-application
        //      /sample-application
        String applicationName = null;
        //      sample-subdomain.sample-domain.com:9091/sample-application
        if (ApplicationDescriptionHandler.getInstance().applicationExists(hostname + port + uri)) {
            applicationName = hostname + port + uri;
        }

        //      sample-subdomain.sample-domain.com/sample-application
        else if (ApplicationDescriptionHandler.getInstance().applicationExists(uri)) {
            applicationName = uri;
        }

        try {
            return ApplicationDescriptionHandler.getInstance().getApplication(applicationName);
        } catch (NoApplicationAvailableException e) {
            log("no application available, e: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void log(String log) {
        logger.info(log);
    }

}

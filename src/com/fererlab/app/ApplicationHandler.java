package com.fererlab.app;

import com.fererlab.dto.*;

import java.util.logging.Logger;

/**
 * acm | 12/12/12 12:01 AM
 */
public class ApplicationHandler {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public Response runApplication(Request request) {
        try {
            if (request.getParams().containsKey(RequestKeys.URI.getValue())) {
                String uri = (String) request.getParams().get(RequestKeys.URI.getValue()).getValue();
                log("found uri: " + uri);
                Application application = findApplicationFromURI(uri);
                if (application != null) {
                    log("found application for this uri: " + uri + ", will run application");
                    return application.runApplication(request);
                } else {
                    log("no application assigned for this URI: " + uri + ", no application to run");
                }
            } else {
                log("request does not have any param named 'URI', no application to run");
            }
            return new Response(new ParamMap<>(), new Session(), Status.STATUS_NOT_FOUND, "");
        } catch (Exception e) {
            return new Response(new ParamMap<>(), new Session(), Status.STATUS_SERVICE_UNAVAILABLE, "");
        }
    }

    private Application findApplicationFromURI(String uri) {
        String applicationName = uri;
        for(String part: uri.split("/")){
            if(!part.isEmpty()){
                applicationName = part;
                break;
            }
        }
        if (ApplicationDescriptionHandler.getInstance().applicationExists(applicationName)) {
            try {
                return ApplicationDescriptionHandler.getInstance().getApplication(applicationName);
            } catch (NoApplicationAvailableException e) {
                log("no application available, e: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private void log(String log) {
        logger.info(log);
    }

}

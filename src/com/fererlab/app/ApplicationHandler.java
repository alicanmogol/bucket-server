package com.fererlab.app;

import com.fererlab.dto.*;

import java.util.logging.Logger;

/**
 * acm | 12/12/12
 */
public class ApplicationHandler {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public Response runApplication(Request request, ApplicationDescriptionHandler adh) {
        try {
            String domainName = String.valueOf(request.getHeaders().get(RequestKeys.HOST_NAME.getValue()).getValue());
            // domains=localhost,fererlab.com,acmbay.com
            // fererlab.com
            if (adh.domainExists(domainName)) {
                log("this domain exists: " + domainName);
                String applicationName = "";
                String[] uriParts = (String.valueOf(request.getParams().get(RequestKeys.URI.getValue()).getValue())).split("/");
                if (uriParts.length > 1) {
                    applicationName = uriParts[1].trim();
                }
                // fererlab.com
                // cms
                // fererlab.com.applications=cms,sample

                String currentRequestURI = request.getParams().getValue(RequestKeys.URI.getValue()).toString();
                if (currentRequestURI.startsWith("/" + applicationName)) {
                    currentRequestURI = currentRequestURI.substring(("/" + applicationName).length());
                    Param<String, Object> param = new Param<String, Object>(
                            RequestKeys.URI.getValue(),
                            currentRequestURI
                    );
                    request.getParams().put(RequestKeys.URI.getValue(), param);
                    log("request URI for this application changed to: \"" + request.getParams().get(RequestKeys.URI.getValue()).getValue() + "\"");
                }

                if (adh.applicationExists(domainName, applicationName)) {
                    log("will run the application: " + applicationName + " for domain: " + domainName);
                    // fererlab.com     cms     request
                    return adh.runApplication(domainName, applicationName, request);
                } else {
                    log("this application: " + applicationName + " does not exists for this domain: " + domainName + " will try to load default");
                    if (adh.hasDefaultApplication(domainName)) {
                        applicationName = adh.getDefaultApplication(domainName);
                        return adh.runApplication(domainName, applicationName, request);
                    } else {
                        log("current domain does not have a default application");
                    }
                }
            } else {
                log("this domain does not exists: " + domainName);
            }
            log("will return not found message");
            return new Response(new ParamMap<String, Param<String, Object>>(), new Session(""), Status.STATUS_NOT_FOUND, "");
        } catch (Exception e) {
            log("Exception occurred while running the application, will return service unavailable message, e: " + e.getMessage());
            return new Response(new ParamMap<String, Param<String, Object>>(), new Session(""), Status.STATUS_SERVICE_UNAVAILABLE, "");
        }
    }

    private void log(String log) {
        logger.info(log);
    }

}

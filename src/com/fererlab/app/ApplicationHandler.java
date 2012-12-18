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
            return new Response(new ParamMap<String, Param<String, Object>>(), new Session(), Status.STATUS_NOT_FOUND, "");
        } catch (Exception e) {
            return new Response(new ParamMap<String, Param<String, Object>>(), new Session(), Status.STATUS_SERVICE_UNAVAILABLE, "");
        }
    }

    private Application findApplicationFromURI(String uri) {
        // TODO: find the application / flow and run that with request

        return new Application() {
            @Override
            public Response runApplication(Request request) {
                final String content = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        " <head>\n" +
                        " </head>\n" +
                        " <body>\n" +
                        "   <p>Page content...</p>\n" +
                        " </body>\n" +
                        "</html>\n";
                return new Response(
                        new ParamMap<String, Param<String, Object>>() {{
                            addParam(new Param<String, Object>(ResponseKeys.STATUS.getValue(), "" + Status.STATUS_SUCCESS.getStatus()));
                            addParam(new Param<String, Object>(ResponseKeys.MESSAGE.getValue(), Status.STATUS_SUCCESS.getMessage()));
                            addParam(new Param<String, Object>(ResponseKeys.EXPIRES.getValue(), "-1"));
                            addParam(new Param<String, Object>(ResponseKeys.CACHE_CONTROL.getValue(), "Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0, private, max-age=0"));
                            addParam(new Param<String, Object>(ResponseKeys.SERVER.getValue(), "bucket"));
                            addParam(new Param<String, Object>(ResponseKeys.CONTENT_TYPE.getValue(), "text/html; charset=UTF-8"));
                            addParam(new Param<String, Object>(ResponseKeys.CONTENT_LENGTH.getValue(), content.length() + 4)); // 4 is the number of the delimiter chars; \n\r\n\r
                        }},
                        new Session(),
                        Status.STATUS_SUCCESS,
                        content
                );
            }
        };
    }

    private void log(String log) {
        logger.info(log);
    }

}

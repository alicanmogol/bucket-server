package com.fererlab.app;

import com.fererlab.dto.*;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * acm | 12/12/12 12:01 AM
 */
public class ApplicationHandler {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    public Response runApplication(Request request) {
        try {
            if (true || request.getParams().contains(RequestKeys.URI.getValue())) {
                String uri = request.getParams().get(RequestKeys.URI.getValue());
                Application application = findApplicationFromURI(uri);
                if (application != null) {
                    return application.runApplication(request);
                }
            }
            return new Response(Arrays.asList(new Param[]{}), new Session(), Status.STATUS_NOT_FOUND, "");
        } catch (Exception e) {
            return new Response(Arrays.asList(new Param[]{}), new Session(), Status.STATUS_SERVICE_UNAVAILABLE, "");
        }
    }

    private Application findApplicationFromURI(String uri) {
        // TODO: find the application / flow and run that with request

        return new Application() {
            @Override
            public Response runApplication(Request request) {
                return new Response(
                        Arrays.asList(new Param[]{}),
                        new Session(),
                        Status.STATUS_SUCCESS,
                        "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                " <head>\n" +
                                " </head>\n" +
                                " <body>\n" +
                                "   <p>Page content...</p>\n" +
                                " </body>\n" +
                                "</html>\n");
            }
        };
    }

    private void log(String log) {
        logger.info(log);
    }

}

package com.etrade.app;

import com.fererlab.app.Application;
import com.fererlab.dto.*;

/**
 * acm | 1/5/13 10:04 AM
 */
public class SampleApplication implements Application {

    @Override
    public Response runApplication(final Request request) {
        final String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head>\n" +
                " </head>\n" +
                " <body>\n" +
                "   <p>Sample content...</p>\n" +
                " </body>\n" +
                "</html>\n";

        ParamMap<String, Param<String, Object>> paramMap = new ParamMap<>();
        paramMap.addParam(new Param<String, Object>("application-added-header", "application-added-value"));

        Session<String, String> session = new Session<>();
        session.put("application-added-cookie-key", "application-added-cookie-value");

        return new Response(
                paramMap,
                session,
                Status.STATUS_SUCCESS,
                content
        );

    }
}

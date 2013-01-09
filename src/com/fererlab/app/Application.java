package com.fererlab.app;

import com.fererlab.dto.Request;
import com.fererlab.dto.Response;

/**
 * acm | 12/11/12 11:31 PM
 */
public interface Application {

    Response runApplication(final Request request);

}

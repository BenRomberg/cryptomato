package com.benromberg.cryptomato;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("Failure when processing request.", exception);
        return Response.serverError().build();
    }
}
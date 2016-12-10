package com.benromberg.cryptomato;

import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jrestless.aws.gateway.GatewayResourceConfig;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;

public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        init(getResourceConfig());
        start();
    }

    public static ResourceConfig getResourceConfig() {
        return new GatewayResourceConfig().register(createJsonProvider()).packages(
                RequestHandler.class.getPackage().getName());
    }

    public static JacksonJaxbJsonProvider createJsonProvider() {
        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.findAndRegisterModules();
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        jsonProvider.setMapper(jsonMapper);
        return jsonProvider;
    }
}
package com.benromberg.cryptomato;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Guice;
import com.jrestless.aws.gateway.GatewayResourceConfig;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        init(getResourceConfig());
        start();
    }

    public static ResourceConfig getResourceConfig() {
        return new GatewayResourceConfig()
                .register(createJsonProvider())
                .register(GuiceFeature.class)
                .packages(RequestHandler.class.getPackage().getName());
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

    private static class GuiceFeature implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
            GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(Guice.createInjector());
            return true;
        }
    }
}
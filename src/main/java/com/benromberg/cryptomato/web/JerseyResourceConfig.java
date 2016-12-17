package com.benromberg.cryptomato.web;

import com.benromberg.cryptomato.core.JsonMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Guice;
import com.jrestless.aws.gateway.GatewayResourceConfig;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class JerseyResourceConfig extends GatewayResourceConfig {
    public JerseyResourceConfig() {
        register(createJsonProvider());
        register(GuiceFeature.class);
        packages(JerseyResourceConfig.class.getPackage().getName());
    }

    public static JacksonJaxbJsonProvider createJsonProvider() {
        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
        jsonProvider.setMapper(JsonMapper.INSTANCE);
        return jsonProvider;
    }

    private static class GuiceFeature implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
            GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(Guice.createInjector(new GuiceModule()));
            return true;
        }
    }
}

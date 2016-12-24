package com.benromberg.cryptomato.web;

import com.benromberg.cryptomato.core.JsonMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class JerseyResourceConfig extends ResourceConfig {
    private final GuiceFeature guiceFeature;

    public JerseyResourceConfig(Module module) {
        register(createJsonProvider());
        guiceFeature = new GuiceFeature(module);
        register(guiceFeature);
        packages(JerseyResourceConfig.class.getPackage().getName());
    }

    public static JacksonJaxbJsonProvider createJsonProvider() {
        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
        jsonProvider.setMapper(JsonMapper.INSTANCE);
        return jsonProvider;
    }

    public Injector getInjector() {
        return guiceFeature.injector;
    }

    private static class GuiceFeature implements Feature {
        private final Module module;
        private Injector injector;

        public GuiceFeature(Module module) {
            this.module = module;
        }

        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
            GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            injector = Guice.createInjector(module);
            guiceBridge.bridgeGuiceInjector(injector);
            return true;
        }
    }
}

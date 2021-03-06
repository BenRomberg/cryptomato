package com.benromberg.cryptomato;

import com.benromberg.cryptomato.web.GuiceModule;
import com.benromberg.cryptomato.web.JerseyResourceConfig;
import com.jrestless.aws.gateway.GatewayFeature;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;

public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        JerseyResourceConfig resourceConfig = new JerseyResourceConfig(new GuiceModule());
        resourceConfig.register(GatewayFeature.class);
        init(resourceConfig);
        start();
    }
}
package com.benromberg.cryptomato;

import com.benromberg.cryptomato.web.JerseyResourceConfig;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;

public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        init(new JerseyResourceConfig());
        start();
    }
}
package com.benromberg.cryptomato;

import com.jrestless.aws.gateway.GatewayResourceConfig;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;

public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        init(new GatewayResourceConfig().packages(getClass().getPackage().getName()));
        start();
    }
}
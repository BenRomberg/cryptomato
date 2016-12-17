package com.benromberg.cryptomato.web.view;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TimestampTokenRequest {
    @JsonProperty
    private final String username;

    @JsonProperty
    private final String publicKey;

    @JsonCreator
    public TimestampTokenRequest(String username, String publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return publicKey;
    }
}

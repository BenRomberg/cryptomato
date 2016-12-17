package com.benromberg.cryptomato.web.view;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TimestampTokenResponse {
    @JsonProperty
    private final String encodedToken;

    @JsonProperty
    private final String username;

    @JsonProperty
    private final LocalDateTime time;

    @JsonCreator
    public TimestampTokenResponse(String username, LocalDateTime time, String encodedToken) {
        this.username = username;
        this.time = time;
        this.encodedToken = encodedToken;
    }

    public String getEncodedToken() {
        return encodedToken;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}

package com.benromberg.cryptomato.web.view;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ColdstartResponse {
    @JsonProperty
    private final int counter;

    @JsonCreator
    public ColdstartResponse(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
}
package com.benromberg.cryptomato.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class User extends Entity<String> {
    @JsonProperty
    private final String name;

    @JsonProperty
    private final String publicKey;

    @JsonProperty
    private final String token;

    @JsonProperty
    private final LocalDateTime time;

    @JsonCreator
    public User(String name, String publicKey, String token, LocalDateTime time) {
        super(UUID.randomUUID().toString());
        this.name = name;
        this.publicKey = publicKey;
        this.token = token;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getTime() {
        return time;
    }
}

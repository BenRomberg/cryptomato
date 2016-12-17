package com.benromberg.cryptomato.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Configuration {
    @JsonProperty
    private Map<String, String> dynamoDbTables;

    @JsonCreator
    public Configuration(Map<String, String> dynamoDbTables) {
        this.dynamoDbTables = dynamoDbTables;
    }

    public String getDynamoDbTableName(String key) {
        return dynamoDbTables.get(key);
    }
}

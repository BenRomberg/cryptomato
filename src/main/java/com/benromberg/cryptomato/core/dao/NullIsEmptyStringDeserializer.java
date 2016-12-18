package com.benromberg.cryptomato.core.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class NullIsEmptyStringDeserializer extends StdDeserializer<String> {
    protected NullIsEmptyStringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        return parser.getValueAsString();
    }

    @Override
    public String getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return "";
    }
}

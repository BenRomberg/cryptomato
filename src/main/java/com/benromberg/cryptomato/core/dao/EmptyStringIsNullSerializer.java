package com.benromberg.cryptomato.core.dao;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class EmptyStringIsNullSerializer extends StdSerializer<String> {

    protected EmptyStringIsNullSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
            JsonProcessingException {
        if (value == null || value.isEmpty()) {
            gen.writeNull();
            return;
        }
        gen.writeString(value);
    }
}

package com.benromberg.cryptomato.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URL;

public class JsonMapper {
    public static final ObjectMapper INSTANCE = createInstance();

    private static ObjectMapper createInstance() {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.findAndRegisterModules();
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        return jsonMapper;
    }

    public static <T> T fromClasspath(String path, Class<T> targetClass) throws IOException {
        URL configurationUri = JsonMapper.class.getClassLoader().getResource(path);
        return JsonMapper.INSTANCE.readValue(configurationUri, targetClass);
    }
}

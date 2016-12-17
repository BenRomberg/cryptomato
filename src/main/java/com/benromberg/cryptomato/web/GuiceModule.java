package com.benromberg.cryptomato.web;

import com.benromberg.cryptomato.core.JsonMapper;
import com.benromberg.cryptomato.model.Configuration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;
import java.io.IOException;

public class GuiceModule extends AbstractModule {
    @Provides
    @Singleton
    public Configuration getConfiguration() throws IOException {
        return JsonMapper.fromClasspath("configuration.json", Configuration.class);
    }

    @Override
    protected void configure() {
    }
}

package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ProviderOptions {
    private final String name;
    private final Map<String, String> params;

    @JsonCreator
    public ProviderOptions(@JsonProperty("name") String name,
                           @JsonProperty("params") Map<String, String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public String getString(String key) {
        return this.params.get(key);
    }

    public Integer getInt(String key) {
        return Integer.parseInt(getString(key));
    }

}

package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CoreOptions {
    private final Map<String, String> params;

    @JsonCreator
    public CoreOptions(@JsonProperty("params") Map<String, String> params) {
        this.params = params;
    }

    public String getString(String key) {
        return this.params.get(key);
    }

    public Integer getInt(String key) {
        return Integer.parseInt(getString(key));
    }

}

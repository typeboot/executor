package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ProviderOptions {
    private final String name;
    private final Map<String, String> params;
    private final String extension;
    private final String separator;

    @JsonCreator
    public ProviderOptions(@JsonProperty("name") String name,
                           @JsonProperty(value = "extension", defaultValue = "sql") String ext,
                           @JsonProperty("params") Map<String, String> params,
                           @JsonProperty(value = "separator", defaultValue = ";") String separator) {
        this.name = name;
        this.params = params;
        this.extension = ext;
        this.separator = separator;
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

    public String getExtension() {
        return extension;
    }

    public String getSeparator() {
        return separator;
    }
}

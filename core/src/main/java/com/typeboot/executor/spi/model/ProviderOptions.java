package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.typeboot.executor.spi.utils.FileUtils;

import java.util.Map;
import java.util.Optional;

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
        return Optional.ofNullable(this.params.get(key)).orElseThrow(() -> new RuntimeException(String.format("%s is null", key)));
    }

    public String getString(String key, String defaultValue) {
        return Optional.ofNullable(this.params.get(key)).orElse(defaultValue);
    }

    public String getPassword(String key) {
        String value = getString(key);
        return Optional.ofNullable(value).map(v -> {
            if (v.startsWith("fs:")) {
                return FileUtils.readContent(v.substring(3));
            } else {
                return v;
            }
        }).orElse("");
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

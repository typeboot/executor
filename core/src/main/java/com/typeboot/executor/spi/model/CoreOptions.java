package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoreOptions {
    private final String source;
    private final ProviderOptions provider;

    @JsonCreator
    public CoreOptions(@JsonProperty("source") String source,
                       @JsonProperty("provider") ProviderOptions provider) {
        this.source = source;
        this.provider = provider;
    }

    public String getString(String key) {
        return this.provider.getString(key);
    }

    public Integer getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public String getSource() {
        return source;
    }

    public ProviderOptions getProvider() {
        return provider;
    }
}

package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ExecutorConfig {
    private final CoreOptions executor;
    private final ProviderOptions provider;

    @JsonCreator
    public ExecutorConfig(@JsonProperty("executor") CoreOptions executor,
                          @JsonProperty("provider") ProviderOptions provider) {
        this.executor = executor;
        this.provider = provider;
    }

    public CoreOptions getExecutor() {
        return executor;
    }

    public ProviderOptions getProvider() {
        return provider;
    }
}

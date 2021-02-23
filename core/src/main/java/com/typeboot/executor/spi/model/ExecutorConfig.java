package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ExecutorConfig {
    private final CoreOptions executor;
    private final ProviderOptions tracker;

    @JsonCreator
    public ExecutorConfig(@JsonProperty("executor") CoreOptions executor,
                          @JsonProperty("provider") ProviderOptions provider) {
        this.executor = executor;
        this.tracker = provider;
    }

    public CoreOptions getExecutor() {
        return executor;
    }

    public ProviderOptions getTracker() {
        return tracker;
    }
}

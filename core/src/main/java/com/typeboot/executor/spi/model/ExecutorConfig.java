package com.typeboot.executor.spi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


public class ExecutorConfig {
    private final CoreOptions executor;
    private final ProviderOptions tracker;
    private final Map<String, String> vars;

    @JsonCreator
    public ExecutorConfig(@JsonProperty("executor") CoreOptions executor,
                          @JsonProperty("provider") ProviderOptions provider,
                          @JsonProperty("vars") Map<String, String> vars) {
        this.executor = executor;
        this.tracker = provider;
        this.vars = vars;
    }

    public CoreOptions getExecutor() {
        return executor;
    }

    public ProviderOptions getTracker() {
        return tracker;
    }

    public Map<String, String> getVars() {
        return vars;
    }
}

package com.typeboot.executor.spi;

import com.typeboot.executor.spi.model.ProviderOptions;


public interface ScriptExecutorFactory {
    ScriptExecutor create(ProviderOptions config);
    String provider();
}

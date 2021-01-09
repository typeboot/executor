package com.typeboot.executor.spi;

import com.typeboot.executor.spi.model.ScriptStatement;

public interface ScriptExecutor {
    boolean executeStatement(ScriptStatement stmt);
}

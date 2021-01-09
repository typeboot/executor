package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions

class JdbcScriptExecutorFactory : ScriptExecutorFactory {
    override fun create(config: ProviderOptions): ScriptExecutor {
        return JdbcExecutor(config)
    }

    override fun provider(): String = "jdbc"
}
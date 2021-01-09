package com.typeboot.executor.cassandra

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions

class CassandraExecutorFactory:ScriptExecutorFactory {
    override fun create(config: ProviderOptions): ScriptExecutor {
        return CassandraExecutor(config)
    }

    override fun provider(): String {
        return "cassandra"
    }
}
package com.typeboot.executor.spi.test

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions

class ScriptExecutorFactoryTest : ScriptExecutorFactory {

    override fun create(config: ProviderOptions?): ScriptExecutor {
        return ScriptExecutor { scriptStatement ->
            println("script statement ${scriptStatement.content}")
            true
        }
    }

    override fun provider(): String = "test"
}
package com.typeboot.executor.spi.test

import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement

class TestScriptExecutor : ScriptExecutor {

    override fun executeStatement(scriptStatement: ScriptStatement): Boolean {
        println("script statement ${scriptStatement.content}")
        return true
    }

    override fun queryForObject(stmt: ScriptStatement?, rowMapper: RowMapper?): Any {
        return ""
    }

    override fun shutdown() {
    }
}

class ScriptExecutorFactoryTest : ScriptExecutorFactory {

    override fun create(config: ProviderOptions?): ScriptExecutor {
        return TestScriptExecutor()
    }

    override fun provider(): String = "test"
}
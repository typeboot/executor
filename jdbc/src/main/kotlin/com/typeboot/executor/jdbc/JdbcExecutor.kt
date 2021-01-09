package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement

class JdbcExecutor(params: ProviderOptions) : ScriptExecutor {

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        return false
    }
}
package com.typeboot.executor.cassandra

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement

class CassandraExecutor(options: ProviderOptions) : ScriptExecutor {

    init {

    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        println("cassandra: serial=${stmt.serialNumber}, name=${stmt.name}, content=\"${stmt.content}\"")
        return true
    }
}
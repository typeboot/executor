package com.typeboot.executor.cassandra

import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement

class CassandraExecutor(private val options: ProviderOptions) : ScriptExecutor {

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        println("cassandra: serial=${stmt.serialNumber}, name=${stmt.name}, content=\"${stmt.content}\"")
        return true
    }

    override fun queryForObject(stmt: ScriptStatement?, rowMapper: RowMapper?): Any {
        TODO("Not yet implemented")
    }
}
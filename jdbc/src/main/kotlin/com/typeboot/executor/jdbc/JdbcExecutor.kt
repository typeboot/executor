package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.WrappedRow
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class JdbcExecutor(provider: ProviderOptions) : ScriptExecutor {

    private fun getStringOrThrow(provider: ProviderOptions, param: String): String {
        return provider.getString(param) ?: throw RuntimeException("requires $param")
    }

    private var conn: Connection

    init {
        val username = getStringOrThrow(provider, "username")
        val password = getStringOrThrow(provider, "password")
        val port = getStringOrThrow(provider, "port")
        val host = getStringOrThrow(provider, "host")

        val props = Properties().apply {
            setProperty("user", "$username")
            setProperty("password", "$password")
        }
        Class.forName(getStringOrThrow(provider, "driver"))
        this.conn = DriverManager.getConnection("jdbc:postgresql://$host:$port/", props)
        println("DB initialisation")
    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with(this.conn) {
            try {
                println("execute statement: [${stmt.content}]")
                createStatement().execute(stmt.content)
            } catch (se: SQLException) {
                throw RuntimeException("error executing statements", se)
            }
        }
        return true
    }

    override fun queryForObject(script: ScriptStatement, rowMapper: RowMapper): Any? {
        val stmt = this.conn.createStatement()
        with(stmt) {
            println("executing script: ${script.content}")
            val rs = executeQuery(script.content)
            if (rs.next()) {
                return rowMapper.map(WrappedRow(rs))
            }
        }
        return null
    }
}
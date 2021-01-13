package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class JdbcExecutor(provider: ProviderOptions) : ScriptExecutor {

    private fun getStringOrThrow(provider: ProviderOptions, param: String): String {
        return provider.getString(param)
                ?: throw RuntimeException("requires username and password")
    }

    private var conn: Connection
    init {
        val username = getStringOrThrow(provider, "username")
        val password = getStringOrThrow(provider, "password")
        val port = getStringOrThrow(provider, "port")
        val host = getStringOrThrow(provider, "host")

        val props = Properties().apply {
            setProperty("user", "$username")
            setProperty("password","$password")
        }
        this.conn = DriverManager.getConnection("jdbc:postgresql://$host:$port/test", props)
        print("DB initialisation")
    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with (this.conn) {
            try {
                createStatement().execute(stmt.content)
            } catch (se: SQLException) {
                print(se)
                throw RuntimeException("error executing statements")
            }
        }
        return true
    }
}
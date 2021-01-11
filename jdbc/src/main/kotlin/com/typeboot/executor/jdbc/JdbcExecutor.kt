package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class JdbcExecutor(provider: ProviderOptions) : ScriptExecutor {
    private var conn: Connection
    init {
        val re = RuntimeException("requires username and password")
        val username = provider.getString("username") ?: throw re
        val password = provider.getString("password") ?: throw re
        val props = Properties().apply {
            setProperty("user", "$username")
            setProperty("password","$password")
        }
        this.conn = DriverManager.getConnection("jdbc:postgresql://localhost/test", props)
        print("DB initialisation")
    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with (this.conn) {
            try {
                createStatement().execute(stmt.content)
            } catch (se: SQLException) {
                print(se)
                throw RuntimeException("error executing statements")
                return false
            }
        }
        return true
    }
}
package com.typeboot.executor.jdbc

import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.WrappedRow
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class JdbcExecutor(provider: ProviderOptions) : ScriptExecutor {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JdbcExecutor::class.java)
    }


    private var conn: Connection

    init {
        val username = provider.getString("username");
        val password = provider.getPassword("password");
        val port = provider.getInt("port");
        val host = provider.getString("host");
        val database = provider.getString("database", "postgres");

        val props = Properties().apply {
            setProperty("user", "$username")
            setProperty("password", "$password")
        }
        Class.forName(provider.getString("driver"))
        val url = "jdbc:postgresql://$host:$port/$database"
        LOGGER.info("event_source=jdbc-executor, task=prepare-url, url=$url")
        this.conn = DriverManager.getConnection(url, props)
        LOGGER.info("event_source=jdbc-executor, task=jdbc-connection, result=db-initialised")
    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with(this.conn) {
            try {
                createStatement().execute(stmt.content)
                if(LOGGER.isInfoEnabled) {
                    LOGGER.info("""event_source=jdbc-executor, task=execute-statement, statement_no=${stmt.serialNumber}, script="${stmt.name}", result=success""")
                }
            } catch (se: SQLException) {
                LOGGER.error("""event_source=jdbc-executor, task=execute-statement, statement_no=${stmt.serialNumber}, script="${stmt.name}", result=failure""")
                throw RuntimeException("error executing statements", se)
            }
        }
        return true
    }

    override fun queryForObject(script: ScriptStatement, rowMapper: RowMapper): Any? {
        val stmt = this.conn.createStatement()
        with(stmt) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("event_source=jdbc-executor, task=query, script_no=${script.serialNumber}, statement=${script.content}")
            }
            val rs = executeQuery(script.content)
            if (rs.next()) {
                return rowMapper.map(WrappedRow(rs))
            }
        }
        return null
    }

    override fun shutdown() {
        if (!this.conn.isClosed) {
            this.conn.close()
        }
    }
}
package com.typeboot.executor.cassandra

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.CqlSession
import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ScriptStatement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

class CassandraExecutor(private val cqlSession: CqlSession, private val timeout: Long, private val consistencyLevel: ConsistencyLevel) : ScriptExecutor {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CassandraExecutor::class.java)
    }

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with(this.cqlSession) {
            try {
                val bounded = prepare(stmt.content).bind()
                        .setTimeout(Duration.ofSeconds(timeout))
                        .setConsistencyLevel(consistencyLevel)
                execute(bounded)
                LOGGER.debug("""event_source=cassandra-executor, task=execute-statement, statement_no=${stmt.serialNumber}, statement="${stmt.content}", result=success """)
            } catch (se: Exception) {
                LOGGER.error("""event_source=cassandra-executor, task=execute-statement, statement_no=${stmt.serialNumber}, statement="${stmt.content}", error="${se.message}", result=failure""")
                throw RuntimeException("error executing statements, ${se.message}", se)
            }
        }
        return true
    }

    override fun queryForObject(stmt: ScriptStatement?, rowMapper: RowMapper?): Any {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        if (!this.cqlSession.isClosed) {
            this.cqlSession.close();
        }
    }
}
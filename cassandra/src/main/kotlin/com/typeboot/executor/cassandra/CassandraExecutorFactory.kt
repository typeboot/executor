package com.typeboot.executor.cassandra

import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext

class CassandraExecutorFactory : ScriptExecutorFactory {
    override fun create(provider: ProviderOptions): ScriptExecutor {

        val username = provider.getString("username")
        val password = provider.getPassword("password")
        val port = provider.getInt("port")
        val host = provider.getString("contact_points").split(",").map { InetSocketAddress(it.trim(), port) }
        val ssl = provider.getString("ssl").toBoolean()
        val dc = provider.getString("dc")
        val cqlSessionBuilder = CqlSessionBuilder()
                .addContactPoints(host)
                .withLocalDatacenter(dc)
                .withAuthCredentials(username, password)

        val cqlSession = if (ssl) {
            cqlSessionBuilder.withSslContext(SSLContext.getDefault()).build()
        } else {
            cqlSessionBuilder.build()
        }
        return CassandraExecutor(cqlSession)
    }

    override fun provider(): String {
        return "cassandra"
    }
}
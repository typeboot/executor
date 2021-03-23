package com.typeboot.executor.cassandra

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ProviderOptions
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class CassandraExecutorFactory : ScriptExecutorFactory {
    override fun create(provider: ProviderOptions): ScriptExecutor {

        val username = provider.getString("username")
        val password = provider.getPassword("password")
        val port = provider.getInt("port")
        val host = provider.getString("contact_points").split(",").map { InetSocketAddress(it.trim(), port) }
        val ssl = provider.getString("ssl").toBoolean()
        val dc = provider.getString("dc")
        val truststorePath = provider.getString("truststore_path", "")
        val truststorePassword = provider.getString("truststore_password", "").toCharArray()

        val cqlSessionBuilder = CqlSessionBuilder()
                .addContactPoints(host)
                .withLocalDatacenter(dc)
                .withAuthCredentials(username, password)

        val cqlSession = if (ssl) {
            cqlSessionBuilder.withSslContext(createSslContext(truststorePath, truststorePassword))
                    .build()
        } else {
            cqlSessionBuilder.build()
        }
        val timeout = provider.getString("timeout_in_seconds", "10").toLong()
        val consistency = DefaultConsistencyLevel.valueOf(provider.getString("consistency", "ONE"))
        return CassandraExecutor(cqlSession, timeout, consistency)
    }

    private fun createSslContext(truststorePath: String, truststorePassword: CharArray): SSLContext {
        if (truststorePath != "" && truststorePassword.isNotEmpty()) {
            val truststoreFile = FileInputStream(truststorePath)
            val keystore = KeyStore.getInstance("JKS")
            keystore.load(truststoreFile, truststorePassword)
            val tmf = TrustManagerFactory.getInstance("SunX509")
            tmf.init(keystore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, null)
            return sslContext
        }
        return SSLContext.getDefault()
    }

    override fun provider(): String {
        return "cassandra"
    }
}
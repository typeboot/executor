package com.typeboot.executor.core

import com.typeboot.dataformat.config.YamlSupport
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.ScriptExecutorFactory
import com.typeboot.executor.spi.model.ExecutorConfig
import com.typeboot.executor.spi.model.ProviderOptions
import java.util.ServiceLoader

class Init {
    companion object {
        fun create(fileName: String = ".executor.yaml"): ScriptExecutor {
            val executorConfig = YamlSupport().toInstance(fileName, ExecutorConfig::class.java)
            return executorInstance(executorConfig.provider.name, executorConfig.provider)
        }

        fun executorInstance(name: String, provider: ProviderOptions): ScriptExecutor {
            val factory: ScriptExecutorFactory? = ServiceLoader.load(ScriptExecutorFactory::class.java).find { sf ->
                sf.provider() == name
            }
            return factory?.create(provider)
                    ?: throw RuntimeException("no implementation found for provider [$name]")
        }
    }
}
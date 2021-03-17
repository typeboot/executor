package com.typeboot.executor.core

import com.typeboot.dataformat.config.YamlSupport
import com.typeboot.dataformat.scripts.FileScript
import com.typeboot.dataformat.scripts.FileScripts
import com.typeboot.exceptions.ScriptExecutionException
import com.typeboot.executor.events.*
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ExecutorConfig
import com.typeboot.executor.spi.model.ScriptStatement
import java.io.File
import java.util.regex.Pattern

data class Summary(val totalScripts: Int, val scripts: Int, val statements: Int, val result: Boolean) {
    constructor() : this(0, 0, 0, false)
}

class DefaultRunner(private val instance: ScriptExecutor,
                    private val listener: ExecutionEventListener,
                    private val variables: Map<String, String>) {

    fun run(scripts: List<FileScript>, separator: String) {
        var summary = Summary().copy(totalScripts = scripts.size)
        scripts.forEach { script ->
            var fileContent = File(script.filePath).bufferedReader().readLines().joinToString("\n")
            variables.forEach{(key, value) ->
                val regexStr = "\\{\\{\\s?$key\\s?\\}\\}"
                val variablePattern = Regex(regexStr)
                fileContent = fileContent.replace(variablePattern, value)
            }
            listener.beforeScriptStart(script, fileContent)
            val statements = fileContent.split("$separator").filter { st -> st.trim().isNotEmpty() }
            println("statements ${statements.size}")
            statements.forEach { statement ->
                val scriptStatement = ScriptStatement(script.serial, script.name, statement)
                listener.beforeStatement(script, scriptStatement)
                val statementResult = instance.executeStatement(scriptStatement)
                if (!statementResult) {
                    summary = summary.copy(result = false)
                    val ex = ScriptExecutionException("${script.serial} - ${script.name} failed to execute statement")
                    listener.onAbortStatement(script, scriptStatement, ex)
                    listener.onAbortScript(script, ex)
                    listener.afterAll(summary)
                    throw ex
                }
                listener.afterStatement(script, scriptStatement, statementResult)
                summary = summary.copy(statements = summary.statements + 1)
            }
            listener.afterScriptEnd(script, statements.size)
            summary = summary.copy(scripts = summary.scripts + 1)
        }
        listener.afterAll(summary.copy(result = true))
    }
}

class Runners {

    companion object {
        fun process(entryFile: String) {
            val executorConfig = YamlSupport().toInstance(entryFile, ExecutorConfig::class.java)
            val trackerOptions = executorConfig.tracker
            val trackerExecutor = Init.executorInstance(executorConfig.tracker.name, trackerOptions)

            val coreListener = CoreExecutionEventListener(trackerExecutor, trackerOptions)

            val listeners = listOf<ExecutionEventListener>()
            val wrappedListeners = WrapperExecutionListener(
                    listOf(coreListener, WrapperExecutionListener(listeners, true)))

            wrappedListeners.beforeAll()

            val itemOptions = executorConfig.executor.provider
            val source = executorConfig.executor.source.replace("\$HOME", System.getProperty("user.home"), true)
            val scripts = FileScripts.fromSource(source, itemOptions.extension)

            val alreadyRun = DefaultWatermarkService(trackerExecutor, trackerOptions).watermark(trackerOptions.getString("app_name"))
            val scriptsToRun = scripts.filter { f -> f.serial > alreadyRun }
            println("total scripts ${scripts.size}, selected for run: ${scriptsToRun.size}")
            if (scriptsToRun.isNotEmpty()) {
                val itemExecutor = Init.executorInstance(itemOptions.name, itemOptions)
                DefaultRunner(itemExecutor, wrappedListeners, executorConfig.vars).run(scriptsToRun, itemOptions.separator)
                itemExecutor.shutdown();
            }
            trackerExecutor.shutdown();
        }
    }
}
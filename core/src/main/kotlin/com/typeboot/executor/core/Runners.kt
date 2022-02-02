package com.typeboot.executor.core

import com.typeboot.dataformat.config.JsonSupport
import com.typeboot.dataformat.config.YamlSupport
import com.typeboot.dataformat.scripts.FileScript
import com.typeboot.dataformat.scripts.FileScripts
import com.typeboot.exceptions.ScriptExecutionException
import com.typeboot.executor.core.DefaultRunner.Companion.LOGGER
import com.typeboot.executor.events.*
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ExecutorConfig
import com.typeboot.executor.spi.model.ScriptStatement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

data class Summary(val totalScripts: Int, val scripts: Int, val statements: Int, val result: Boolean) {
    constructor() : this(0, 0, 0, false)
}

class DefaultRunner(private val instance: ScriptExecutor,
                    private val listener: ExecutionEventListener,
                    private val variables: Map<String, String>) {

    companion object {
        val LOGGER :Logger = LoggerFactory.getLogger(DefaultRunner::class.java)
    }

    fun run(scripts: List<FileScript>, separator: String) {
        val resultSummaryMap = emptyMap<String,Map<String,String>>().toMutableMap();
        var summary = Summary().copy(totalScripts = scripts.size)
        scripts.forEach { script ->
            val fileContent = File(script.filePath).bufferedReader().readLines().joinToString("\n")
            var resolvedFileContent = fileContent
            variables.forEach { (key, value) ->
                val regexStr = "\\{\\{\\s?$key\\s?\\}\\}"
                val variablePattern = Regex(regexStr)
                resolvedFileContent = resolvedFileContent.replace(variablePattern, value)
            }
            listener.beforeScriptStart(script, fileContent, variables, resolvedFileContent)

            val statements = resolvedFileContent.split("$separator").filter { st -> st.trim().isNotEmpty() }
            LOGGER.info("event_source=runner-main, task=count-statements-in-file, file_name=${script.name}, total_statements=${statements.size}")
            val statementSummaryMap = mapOf("total_statements" to statements.size.toString(), "statements_ran" to "0", "error_msg" to "" ).toMutableMap()
            resultSummaryMap[script.name] = statementSummaryMap

            statements.forEach { statement ->
                try {
                    val scriptStatement = ScriptStatement(script.serial, script.name, statement)
                    listener.beforeStatement(script, scriptStatement)
                    val statementResult = instance.executeStatement(scriptStatement)
                    if (!statementResult) {
                        summary = summary.copy(result = false)
                        statementSummaryMap["error_msg"] = "failed to execute statement"
                        resultSummaryMap[script.name] = statementSummaryMap
                        val ex = ScriptExecutionException("${script.serial} - ${script.name} failed to execute statement, result_summary=${resultSummaryMap}")
                        listener.onAbortStatement(script, scriptStatement, ex)
                        listener.onAbortScript(script, ex)
                        listener.afterAll(summary)
                        throw ex
                    }
                    listener.afterStatement(script, scriptStatement, statementResult)
                    statementSummaryMap["statements_ran"] = String.format(statementSummaryMap["statements_ran"]?.toInt()?.plus(1).toString())
                    summary = summary.copy(statements = summary.statements + 1)
                }catch (scriptException: Exception){
                    statementSummaryMap["error_msg"] = scriptException.message.toString()
                    resultSummaryMap[script.name] = statementSummaryMap
                    throw RuntimeException("error executing statements, result_summary=${resultSummaryMap}", scriptException)
                }
            }
            listener.afterScriptEnd(script, statements.size)
            summary = summary.copy(scripts = summary.scripts + 1)
            resultSummaryMap[script.name] = statementSummaryMap
        }
        listener.afterAll(summary.copy(result = true))
        LOGGER.info("event_source=runner-main, task=final-state, result_summary=${resultSummaryMap}, message=All scripts ran successfully")
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

            val watermark = DefaultWatermarkService(trackerExecutor, trackerOptions).watermark(trackerOptions.getString("app_name"))
            if (watermark.value < 0) {
                val ignoreUncleanState = executorConfig.tracker.getString("ignore_unclean_state", "false").toBoolean()
                if (ignoreUncleanState) {
                    LOGGER.warn("""event_source=runner-main, task=watermark, status=success, ignore_unclean_state=${ignoreUncleanState}, message="ignore_unclean_state is true. will retry execution by ignoring existing in PROGRESS items."""")
                } else {
                    val ex = (watermark.exception?.let(::Exception) ?: RuntimeException("previous progress check failed."))
                    LOGGER.error("event_source=runner-main, task=watermark, status=failure, exception={}", ex)
                    throw ex
                }
            }

            val scriptsToRun = scripts.filter { f -> f.serial > watermark.value }
            LOGGER.info("event_source=runner-main, task=collect-scripts, total_scripts=${scripts.size}, scripts_to_run=${scriptsToRun.size}")
            if (scriptsToRun.isNotEmpty()) {
                val itemExecutor = Init.executorInstance(itemOptions.name, itemOptions)
                DefaultRunner(itemExecutor, wrappedListeners, executorConfig.vars).run(scriptsToRun, itemOptions.separator)
                itemExecutor.shutdown();
            }else{
                LOGGER.info("event_source=runner-main, task=collect-scripts, message=No scripts to run")
            }

            trackerExecutor.shutdown();
        }
    }
}
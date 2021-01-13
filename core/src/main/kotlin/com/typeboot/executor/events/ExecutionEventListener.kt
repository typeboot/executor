package com.typeboot.executor.events

import com.typeboot.dataformat.scripts.FileScript
import com.typeboot.executor.core.Summary
import com.typeboot.executor.spi.model.ScriptStatement
import java.lang.RuntimeException

interface ExecutionEventListener {
    fun beforeAll()

    fun beforeScriptStart(fileScript: FileScript)

    fun beforeStatement(fileScript: FileScript, statement: ScriptStatement)

    fun afterStatement(fileScript: FileScript, statement: ScriptStatement, result: Boolean)

    fun onAbortStatement(fileScript: FileScript, statement: ScriptStatement, th: Throwable)

    fun onAbortScript(fileScript: FileScript, th: Throwable)

    fun afterScriptEnd(fileScript: FileScript)

    fun afterAll(result: Summary)
}


class WrapperExecutionListener(private val listeners: List<ExecutionEventListener>, private val suppressException: Boolean = false) : ExecutionEventListener {
    private fun safeTry(block: () -> Unit) {
        try {
            block()
        } catch (te: RuntimeException) {
            if (!suppressException) {
                throw te
            }
        }
    }

    override fun beforeAll() {
        safeTry {
            listeners.forEach { it.beforeAll() }
        }
    }

    override fun beforeScriptStart(fileScript: FileScript) {
        safeTry {
            listeners.forEach { it.beforeScriptStart(fileScript) }
        }
    }

    override fun beforeStatement(fileScript: FileScript, statement: ScriptStatement) {
        safeTry {
            listeners.forEach { it.beforeStatement(fileScript, statement) }
        }
    }


    override fun afterStatement(fileScript: FileScript, statement: ScriptStatement, result: Boolean) {
        safeTry {
            listeners.forEach { it.afterStatement(fileScript, statement, result) }
        }
    }


    override fun onAbortStatement(fileScript: FileScript, statement: ScriptStatement, th: Throwable) {
        safeTry {
            listeners.forEach {
                it.onAbortStatement(fileScript, statement, th)
            }
        }
    }

    override fun onAbortScript(fileScript: FileScript, th: Throwable) {
        safeTry {
            listeners.forEach { it.onAbortScript(fileScript, th) }
        }
    }


    override fun afterScriptEnd(fileScript: FileScript) {
        safeTry {
            listeners.forEach { it.afterScriptEnd(fileScript) }
        }
    }

    override fun afterAll(result: Summary) {
        safeTry {
            listeners.forEach { it.afterAll(result) }
        }
    }
}
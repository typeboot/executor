package com.typeboot.executor.events

import com.typeboot.exceptions.ScriptExecutionException
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.WrappedRow
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import java.lang.RuntimeException

interface ScriptWatermark {
    fun watermark(appName: String): Watermark
}

data class Watermark(val value: Int, val exception: RuntimeException? = null)

class DefaultWatermarkService(private val queryExecutor: ScriptExecutor, private val providerOptions: ProviderOptions) : ScriptWatermark {
    override fun watermark(appName: String): Watermark {
        val table = providerOptions.getString("table")
        val schema = providerOptions.getString("schema")

        val hasIncomplete = queryExecutor.queryForObject(ScriptStatement(1, "",
                "select count(script_id) from $schema.$table where app_name='%s' AND status='%s'".format(appName, "PROGRESS"))
        ) { underlying -> (underlying as WrappedRow).getInt(1) }

        val totalInProgress = hasIncomplete as Int
        if (totalInProgress != 0) {
            Watermark(-1, ScriptExecutionException("there are $totalInProgress statement(s) already in progress for application [$appName]. cleanup required."))
        }

        val maxNumber = queryExecutor.queryForObject(ScriptStatement(1, "",
                "select max(script_id) as max_script from $schema.$table where app_name='%s' AND status='%s'".format(appName, "DONE"))
        ) { underlying -> (underlying as WrappedRow).getInt("max_script") }
        return Watermark(maxNumber as Int)
    }
}
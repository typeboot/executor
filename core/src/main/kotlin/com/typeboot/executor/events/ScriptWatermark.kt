package com.typeboot.executor.events

import com.typeboot.exceptions.ScriptExecutionException
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.WrappedRow
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement

interface ScriptWatermark {
    fun watermark(appName: String): Int
}

class DefaultWatermarkService(private val queryExecutor: ScriptExecutor, private val providerOptions: ProviderOptions) : ScriptWatermark {
    override fun watermark(appName: String): Int {
        val table = providerOptions.getString("table")
        val schema = providerOptions.getString("database")

        val hasIncomplete = queryExecutor.queryForObject(ScriptStatement(1, "",
                "select count(script_id) from $schema.$table where app_name='%s' AND status='%s'".format(appName, "PROGRESS"))
        ) { underlying -> (underlying as WrappedRow).getInt(1) }

        val totalInProgress = hasIncomplete as Int
        if (totalInProgress != 0) {
            throw ScriptExecutionException("there are $totalInProgress statement(s) already in progress for application [$appName]. cleanup required.")
        }

        val maxNumber = queryExecutor.queryForObject(ScriptStatement(1, "",
                "select max(script_id) as max_script from $schema.$table where app_name='%s' AND status='%s'".format(appName, "DONE"))
        ) { underlying -> (underlying as WrappedRow).getInt("max_script") }
        return maxNumber as Int
    }
}
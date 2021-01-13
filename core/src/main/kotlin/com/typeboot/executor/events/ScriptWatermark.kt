package com.typeboot.executor.events

import com.typeboot.executor.spi.model.ProviderOptions

interface ScriptWatermark {
    fun watermark(appName: String): Int
}

class DefaultWatermarkService(private val providerOptions: ProviderOptions) : ScriptWatermark {
    override fun watermark(appName: String): Int {
        val table = providerOptions.getString("table")
        val schema = providerOptions.getString("database")
//        val maxNumber = queryExecutor.queryForObject(ScriptStatement(1, "",
//                "select max(script_no) from $schema.$table where app_name='%s' status='%s'".format(appName, "DONE")))
//        return maxNumber as Int
        return 1
    }
}
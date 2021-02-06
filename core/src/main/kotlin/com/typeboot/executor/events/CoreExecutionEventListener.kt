package com.typeboot.executor.events

import com.typeboot.dataformat.scripts.FileScript
import com.typeboot.executor.core.Summary
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ProviderOptions
import com.typeboot.executor.spi.model.ScriptStatement
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CoreExecutionEventListener(private val scriptExecutor: ScriptExecutor, trackerOptions: ProviderOptions, private val provider: ProviderOptions) : ExecutionEventListener {

    private val schema = trackerOptions.getString("database")
    private val table = trackerOptions.getString("table")
    private val appName = provider.getString("app_name")
    private val batchNo = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
    private val team = provider.getString("team_name")

    override fun beforeAll() {
        if (provider.name != "jdbc") {
            throw RuntimeException("only jdbc provider is supported for tracker")
        }
        scriptExecutor.executeStatement(ScriptStatement(1, "", "create schema if not exists $schema"))

        scriptExecutor.executeStatement(
                ScriptStatement(1, "", """create table if not exists $schema.$table(
                    |app_name text not null,
                    |version text,
                    |script_id int,
                    |script_name text,
                    |batch_no text,
                    |executed timestamp not null,
                    |status text not null,
                    |statements int not null,
                    |message text,
                    |template text,
                    |vars text,
                    |content text not null,
                    |team text not null,
                    |primary key(app_name, script_id, script_name, batch_no)
                    |)""".trimMargin())
        )
        scriptExecutor.executeStatement(ScriptStatement(1, "", """create table if not exists $schema.${table}_runs(
            |app_name text not null,
            |batch_no text not null,
            |executed timestamp not null,
            |status text not null,
            |total_scripts int not null,
            |files int not null,
            |statements int not null,
            |primary key(app_name, batch_no)
            |)""".trimMargin()))
    }

    override fun beforeScriptStart(fileScript: FileScript, content: String) {
        val insert = createInserts(
                table,
                listOf("app_name", "script_id", "script_name", "batch_no", "status", "executed", "team", "statements", "content"),
                listOf(appName, fileScript.serial, fileScript.name, batchNo, "PROGRESS", LocalDateTime.now(), team, 0, content)
        )
        scriptExecutor.executeStatement(ScriptStatement(1, "", insert))
    }

    private fun createInserts(tableName: String, cols: List<String>, params: List<Any>): String {
        val insertCols = cols.joinToString(", ")

        val insertParams = params.map { p ->
            when (p) {
                is Int -> "$p"
                is LocalDate -> "'%s'".format(DateTimeFormatter.ISO_LOCAL_DATE.format(p))
                is Date -> "'%s'".format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(p.toInstant().atZone(ZoneId.systemDefault())))
                else -> "'$p'"
            }
        }.joinToString(separator = ", ")
        return "insert into $schema.$tableName($insertCols) values($insertParams)"
    }

    private fun createUpdate(tableName: String, setMap: Map<String, Any>, selectMap: Map<String, Any>): String {
        val setCols = columnValues(setMap, ", ")
        val selectCols = columnValues(selectMap, " AND ")
        return "update $schema.$tableName SET $setCols WHERE $selectCols"
    }

    private fun columnValues(setMap: Map<String, Any>, sep: String) =
            setMap.map { p ->
                val k = p.key
                when (val v = p.value) {
                    is Int -> "$k=$v"
                    is LocalDate -> "$k='%s'".format(DateTimeFormatter.ISO_LOCAL_DATE.format(v))
                    is Date -> "$k='%s'".format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(v.toInstant().atZone(ZoneId.systemDefault())))
                    else -> "$k='$v'"
                }
            }.joinToString(separator = sep)

    override fun beforeStatement(fileScript: FileScript, statement: ScriptStatement) {

    }

    override fun afterStatement(fileScript: FileScript, statement: ScriptStatement, result: Boolean) {

    }

    override fun onAbortStatement(fileScript: FileScript, statement: ScriptStatement, th: Throwable) {
    }

    private fun after(tableName: String, m: Map<String, Any>, fileScript: FileScript) {
        val update = createUpdate(
                tableName,
                m,
                mapOf(
                        "app_name" to appName,
                        "script_id" to fileScript.serial,
                        "script_name" to fileScript.name,
                        "batch_no" to batchNo
                )
        )
        scriptExecutor.executeStatement(ScriptStatement(1, "", update))
    }

    override fun onAbortScript(fileScript: FileScript, th: Throwable) {
        val status = "ERROR"
        val abortMap = mapOf(
                "status" to status,
                "executed" to LocalDateTime.now(),
                "reason" to "aborted ${th.message}"
        )
        after(table, abortMap, fileScript)
    }

    override fun afterScriptEnd(fileScript: FileScript, statements: Int) {
        val status = "DONE"
        val afterMap = mapOf(
                "status" to status,
                "executed" to LocalDateTime.now(),
                "statements" to statements
        )
        after(table, afterMap, fileScript)
    }

    override fun afterAll(summary: Summary) {
        val status = if (summary.result) "DONE" else "ERROR"
        val insert = createInserts(
                "${table}_runs",
                listOf("app_name", "batch_no", "status", "files", "statements", "total_scripts", "executed"),
                listOf(appName, batchNo, status, summary.scripts, summary.statements, summary.totalScripts, LocalDateTime.now())
        )
        scriptExecutor.executeStatement(ScriptStatement(1, "", insert))
    }

}

package com.typeboot.executor.spi.test

import com.typeboot.executor.core.Init
import com.typeboot.executor.spi.model.ScriptStatement

fun main() {
    val executor = Init.create(".test.yaml")
    val result = executor.executeStatement(ScriptStatement(1, "1-create-table.sql",
            "create table a(name text)"))
    println("execution result $result")
}
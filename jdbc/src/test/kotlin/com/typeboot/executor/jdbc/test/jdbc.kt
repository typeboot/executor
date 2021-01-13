package com.typeboot.executor.jdbc.test

import com.typeboot.executor.core.Init
import com.typeboot.executor.spi.model.ScriptStatement

fun main() {
    val executor = Init.create(".jdbc.yaml")
    val result = executor.executeStatement(ScriptStatement(1, "1.create.sql",
            "create table if not exists a(name text)"))
    println("com.typeboot.executor.jdbc output $result")
}
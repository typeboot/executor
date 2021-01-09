package com.typeboot.executor.cassandra.test

import com.typeboot.executor.core.Init
import com.typeboot.executor.spi.model.ScriptStatement

fun main() {
    val executor = Init.create(".cassandra.yaml")
    val res = executor.executeStatement(ScriptStatement(1, "1.user-table.sql", "create table customers.user(name text)"))
    println("result $res")
}
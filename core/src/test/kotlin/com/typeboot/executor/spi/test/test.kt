package com.typeboot.executor.spi.test

import com.typeboot.executor.core.Runners
import com.typeboot.executor.core.Init
import com.typeboot.executor.spi.model.ScriptStatement
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("com.typeboot.executor.spi")
    val executor = Init.create(".test.yaml")
    val result = executor.executeStatement(ScriptStatement(1, "1-create-table.sql",
            "create table a(name text)"))
    logger.info("execution result $result")
    Runners.process(".test.yaml")
}
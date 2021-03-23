package com.typeboot

import com.typeboot.executor.core.Runners
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("com.typeboot.executor.Exec")
    val startTime = System.currentTimeMillis()
    try {
        Runners.process(if (args.size == 1) {
            args[0]
        } else {
            ".jdbc.yaml"
        })
        val timeTaken = Duration.ofMillis(System.currentTimeMillis() - startTime).toSeconds()
        logger.info("event_source=executor, task=run-scripts, result=success, time_taken=${timeTaken}")
        exitProcess(0)
    } catch (ex: RuntimeException) {
        val timeTaken = Duration.ofMillis(System.currentTimeMillis() - startTime).toSeconds()
        logger.error("event_source=executor, task=run-scripts, result=failure, time_taken=${timeTaken}, error={}", ex)
        exitProcess(1)
    }
}

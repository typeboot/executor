package com.typeboot

import com.typeboot.executor.core.Runners
import java.lang.RuntimeException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        Runners.process(if (args.size == 1) {
            args[0]
        } else {
            ".jdbc.yaml"
        })
        exitProcess(0)
    } catch (ex: RuntimeException) {
        exitProcess(1)
    }
}

package com.typeboot

import com.typeboot.executor.core.Runners

fun main(args: Array<String>) {
    val filename = if (args.size == 1) {
        args[0]
    } else {
        ".jdbc.yaml"
    }
    Runners.process(filename)
}

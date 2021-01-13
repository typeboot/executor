package com.typeboot.exceptions

import java.lang.RuntimeException

class ScriptExecutionException(reason: String?, cause: Throwable?) : RuntimeException(reason, cause) {
    constructor(reason: String) : this(reason, null)
}
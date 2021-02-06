package com.typeboot.executor.spi

import java.sql.ResultSet

class WrappedRow(private val row: ResultSet) {
    fun getInt(name: String): Int {
        return row.getInt(name)
    }

    fun getInt(idx: Int): Int {
        return row.getInt(idx)
    }

    fun getString(name: String): String {
        return row.getString(name)
    }
}

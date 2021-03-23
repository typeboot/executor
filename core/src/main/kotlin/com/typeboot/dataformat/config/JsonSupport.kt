package com.typeboot.dataformat.config

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

class JsonSupport {

    companion object {
        private val jsonMapper = ObjectMapper()
        private val factory = JsonFactory()

        init {
            jsonMapper.registerModule(KotlinModule())
        }

        fun toMap(fileName: String): Map<*, *> {
            return jsonMapper.readValue(File(fileName), Map::class.java)
        }

        fun toList(fileName: String): List<ObjectNode> {
            val parser = factory.createParser(File(fileName))
            val typeRef = object : TypeReference<ObjectNode>() {}
            return jsonMapper.readValues(parser, typeRef).readAll()
        }

        fun <T> toInstance(fileName: String, clz: Class<T>): T {
            return jsonMapper.readValue(File(fileName), clz)
        }

        fun <T> toInstance(data: ByteArray, clz: Class<T>): T {
            return jsonMapper.readValue(data, clz)
        }

        fun toJson(o: Any): String {
            return jsonMapper.writeValueAsString(o)
        }
    }
}

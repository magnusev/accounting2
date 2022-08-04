package com.evensberget.accounting.common.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object JsonUtils {

    private val mapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun objectMapper(): ObjectMapper {
        return mapper
    }

    fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        return mapper.treeToValue(jsonNode, clazz)
    }

    fun <T> fromJson(jsonStr: String, clazz: Class<T>): T {
        return mapper.readValue(jsonStr, clazz)
    }

    fun toJson(any: Any): String {
        return mapper.writeValueAsString(any)
    }

}
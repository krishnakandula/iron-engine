package com.krishnakandula.ironengine.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class JsonHelper {

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    inline fun <reified T> readFromFile(filePath: String): T = readFromFile(filePath, T::class.java)

    fun <T> readFromFile(filePath: String, clazz: Class<T>): T {
        val jsonData = File(filePath).readText()
        return objectMapper.readValue(jsonData, clazz)
    }
}
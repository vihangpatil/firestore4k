package dev.vihang.firestore4k.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.cloud.firestore.DocumentSnapshot
import kotlin.reflect.KClass

// Object to Map conversion, and vice-versa.

private val objectMapper = jacksonObjectMapper()

fun <D : Any> D.asMap(): Map<String, Any> =
    objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

fun <D : Any> DocumentSnapshot.toKotlinObject(typeClass: KClass<D>): D? =
    try {
        this.data?.let { objectMapper.convertValue(it, typeClass.java) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

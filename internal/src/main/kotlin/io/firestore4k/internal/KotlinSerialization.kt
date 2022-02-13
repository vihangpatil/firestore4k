package io.firestore4k.internal

import com.google.cloud.firestore.DocumentSnapshot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap

// Object to Map conversion, and vice-versa.

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified D : Any> D.asMap(): Map<String, Any> = Properties.encodeToMap(this)

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified D : Any> DocumentSnapshot.toKotlinObject(): D? =
    try {
        this.data?.let { Properties.decodeFromMap<D>(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

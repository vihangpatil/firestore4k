package dev.vihang.firestore4k.internal

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(@Required val id: String = UUID.randomUUID().toString())

@Serializable
data class Message(@Required val id: String = UUID.randomUUID().toString())

@JvmInline
value class UserId(private val value: String) {
    override fun toString(): String = value
}

@JvmInline
value class MessageId(private val value: String) {
    override fun toString(): String = value
}

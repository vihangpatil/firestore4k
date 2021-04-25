package dev.vihang.firestore4k.typed

import java.util.*

data class User(val id: String = UUID.randomUUID().toString())
data class Message(val id: String = UUID.randomUUID().toString())

@JvmInline
value class UserId(private val value: String) {
    override fun toString(): String = value
}

@JvmInline
value class MessageId(private val value: String) {
    override fun toString(): String = value
}

val users = rootCollection<User, UserId>("users")
val messages = users.subCollection<Message, MessageId>("messages")
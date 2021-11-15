package dev.vihang.firestore4k.dynamic

import java.util.*

data class User(val id: String = UUID.randomUUID().toString())
data class Message(val id: String = UUID.randomUUID().toString())

val users = collection("users")
val messages = collection("messages")
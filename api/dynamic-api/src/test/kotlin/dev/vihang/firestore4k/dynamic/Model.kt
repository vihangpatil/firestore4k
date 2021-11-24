package dev.vihang.firestore4k.dynamic

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(@Required val id: String = UUID.randomUUID().toString())

@Serializable
data class Message(@Required val id: String = UUID.randomUUID().toString())

val users = collection("users")
val messages = collection("messages")
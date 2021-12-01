package dev.vihang.firestore4k.dynamic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

var counter = 0

data class Node(
    val id: Int,
    val children: List<Node>
)

fun createNode(
    count: Int,
    level: Int
): List<Node> {
    return if (level == 0) {
        emptyList()
    } else {
        (1..count).map {
            Node(counter++, createNode(count, level - 1))
        }
    }
}

fun display(node: Node, indent: String) {
    println("$indent${node.id}")
    node.children.forEach {
        display(it, "$indent  ")
    }
}

@ExperimentalCoroutinesApi
suspend fun mockDelete(node: Node, producerScope: ProducerScope<Int>, coroutineScope: CoroutineScope) {
    node.children.reversed().map {
        coroutineScope.async {
            mockDelete(it, producerScope, coroutineScope)
        }
    }
        .awaitAll()
    delay(500)
    println("Found ${node.id}")
    producerScope.send(node.id)
}

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    withContext(Dispatchers.IO) {
        val root = Node(counter++, createNode(10, 2))
        display(root, "")
        channelFlow {
            mockDelete(root, this@channelFlow, this@withContext)
        }
            .buffer(1_000)
            .map {
                async {
                    println("Deleting $it")
                    delay(500)
                }
            }
            .toList(mutableListOf())
            .awaitAll()
        Unit
    }
}
package io.firestore4k.internal

import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
 */
suspend inline fun <reified D : Any> add(collectionPath: String, document: D): String = withContext(Dispatchers.IO) {
    firestoreClient
        .collection(collectionPath)
        .add(document.asMap())
        .await()
        .id
}

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
 */
suspend inline fun <reified D : Any> put(documentPath: String, document: D): Unit = withContext(Dispatchers.IO) {
    firestoreClient
        .document(documentPath)
        .set(document.asMap())
        .await()
    Unit
}

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
 */
suspend inline fun <reified D : Any> get(documentPath: String): D? = withContext(Dispatchers.IO) {
    firestoreClient
        .document(documentPath)
        .get()
        .await()
        .toKotlinObject()
}

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
 */
suspend inline fun <reified D : Any> getAll(collectionPath: String): Collection<D> =
    withContext(Dispatchers.IO) {
        firestoreClient
            .collection(collectionPath)
            .get()
            .await()
            ?.documents
            ?.mapNotNull { it.toKotlinObject() }
            ?: emptyList()
    }

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun deleteCollection(collectionPath: String) = withContext(Dispatchers.IO) {
    channelFlow {
        deleteCollection(
            collectionReference = firestoreClient.collection(collectionPath),
            coroutineScope = this@withContext,
            producerScope = this@channelFlow,
        )
    }
        .buffer(capacity = 1_000)
        .map {
            async {
                it.delete().await()
            }
        }
        .toList(mutableListOf())
        .awaitAll()
    Unit
}

@ExperimentalCoroutinesApi
suspend fun deleteDocument(documentPath: String) = withContext(Dispatchers.IO) {
    channelFlow {
        deleteDocument(
            documentReference = firestoreClient.document(documentPath),
            coroutineScope = this@withContext,
            producerScope = this@channelFlow,
        )
    }
        .buffer(capacity = 1_000)
        .map {
            async {
                it.delete().await()
            }
        }
        .toList(mutableListOf())
        .awaitAll()
    Unit
}

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
 */
@ExperimentalCoroutinesApi
private suspend fun deleteDocument(
    documentReference: DocumentReference,
    coroutineScope: CoroutineScope,
    producerScope: ProducerScope<DocumentReference>,
) {
    documentReference
        .listCollections()
        .map { collectionReference ->
            coroutineScope.async {
                deleteCollection(
                    collectionReference,
                    coroutineScope,
                    producerScope,
                )
            }
        }
        .awaitAll()
    producerScope.send(documentReference)
}

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
 */
@ExperimentalCoroutinesApi
private suspend fun deleteCollection(
    collectionReference: CollectionReference,
    coroutineScope: CoroutineScope,
    producerScope: ProducerScope<DocumentReference>,
) {
    collectionReference
        .listDocuments()
        .map { documentReference ->
            coroutineScope.async {
                deleteDocument(
                    documentReference,
                    coroutineScope,
                    producerScope
                )
            }
        }
        .awaitAll()
}
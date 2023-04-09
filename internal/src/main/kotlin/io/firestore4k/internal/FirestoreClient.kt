package io.firestore4k.internal

import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class FirestoreClient(
    firestoreOptions: FirestoreOptions = FirestoreOptions.getDefaultInstance()
) {

    private val logger = LoggerFactory.getLogger("io.firestore4k.internal.FirestoreClientKt")
    init {
        val firestoreEmulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST")
        if (firestoreEmulatorHost == null) {
            logger.info("Connecting to GCP Firestore")
        } else {
            logger.warn("Connecting to Firestore emulator at $firestoreEmulatorHost")
        }
    }
    val firestore: Firestore = firestoreOptions.service

    /**
     * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
     */
    suspend inline fun <reified D : Any> add(collectionPath: String, document: D): String = withContext(Dispatchers.IO) {
        firestore
            .collection(collectionPath)
            .add(document.asMap())
            .await()
            .id
    }

    /**
     * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
     */
    suspend inline fun <reified D : Any> put(documentPath: String, document: D): Unit = withContext(Dispatchers.IO) {
        firestore
            .document(documentPath)
            .set(document.asMap())
            .await()
        Unit
    }

    /**
     * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
     */
    suspend inline fun <reified D : Any> get(documentPath: String): D? = withContext(Dispatchers.IO) {
        firestore
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
            firestore
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
    suspend fun deleteCollection(collectionPath: String) = withContext(Dispatchers.IO) {
        channelFlow {
            deleteCollection(
                collectionReference = firestore.collection(collectionPath),
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

    suspend fun deleteDocument(documentPath: String) = withContext(Dispatchers.IO) {
        channelFlow {
            deleteDocument(
                documentReference = firestore.document(documentPath),
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
}
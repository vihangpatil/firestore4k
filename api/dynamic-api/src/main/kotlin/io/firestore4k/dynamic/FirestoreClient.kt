package io.firestore4k.dynamic

import com.google.cloud.firestore.FirestoreOptions
import io.firestore4k.internal.FirestoreClient as InternalFirestoreClient

class FirestoreClient(
    firestoreOptions: FirestoreOptions = FirestoreOptions.getDefaultInstance()
) {

    val internalFirestoreClient = InternalFirestoreClient(firestoreOptions)

    /**
     * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
     */
    suspend inline fun <reified D : Any> add(
        path: CollectionPath,
        document: D,
    ): String = internalFirestoreClient.add(
        collectionPath = path.toString(),
        document = document,
    )

    /**
     * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
     */
    suspend inline fun <reified D : Any> put(
        path: DocumentPath,
        document: D,
    ): Unit = internalFirestoreClient.put(
        documentPath = path.toString(),
        document = document,
    )

    /**
     * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
     */
    suspend inline fun <reified D : Any> get(
        path: DocumentPath,
    ): D? = internalFirestoreClient.get(
        documentPath = path.toString(),
    )

    /**
     * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
     */
    suspend inline fun <reified D : Any> getAll(
        path: CollectionPath,
    ): Collection<D> = internalFirestoreClient.getAll(
        collectionPath = path.toString(),
    )

    /**
     * Delete is unsafe because newly added data will not be deleted.
     *
     * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
     */
    suspend fun deleteCollection(
        path: CollectionPath
    ) = internalFirestoreClient.deleteCollection(
        collectionPath = path.toString()
    )

    /**
     * Delete is unsafe because newly added data will not be deleted.
     *
     * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
     */
    suspend fun deleteDocument(
        path: DocumentPath
    ) = internalFirestoreClient.deleteDocument(
        documentPath = path.toString()
    )
}
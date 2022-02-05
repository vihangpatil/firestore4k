package dev.vihang.firestore4k.dynamic

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
 */
suspend inline fun <reified D : Any> add(
    path: CollectionPath,
    document: D,
): String = dev.vihang.firestore4k.internal.add(
    collectionPath = path.toString(),
    document = document,
)

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
 */
suspend inline fun <reified D : Any> put(
    path: DocumentPath,
    document: D,
): Unit = dev.vihang.firestore4k.internal.put(
    documentPath = path.toString(),
    document = document,
)

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
 */
suspend inline fun <reified D : Any> get(path: DocumentPath): D? = dev.vihang.firestore4k.internal.get(
    documentPath = path.toString(),
)

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
 */
suspend inline fun <reified D : Any> getAll(
    path: CollectionPath
): Collection<D> = dev.vihang.firestore4k.internal.getAll(
    collectionPath = path.toString(),
)

/**
 * Delete is unsafe because newly added data will not be deleted.
 *
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun deleteCollection(
    path: CollectionPath
) = dev.vihang.firestore4k.internal.deleteCollection(
    collectionPath = path.toString()
)

/**
 * Delete is unsafe because newly added data will not be deleted.
 *
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun deleteDocument(
    path: DocumentPath
) = dev.vihang.firestore4k.internal.deleteDocument(
    documentPath = path.toString()
)
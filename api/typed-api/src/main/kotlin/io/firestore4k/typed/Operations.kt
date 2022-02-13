package io.firestore4k.typed

import kotlinx.coroutines.ExperimentalCoroutinesApi

suspend inline fun <reified D : Any> add(rootCollection: RootCollection<D, *>, document: D): String =
    add(rootCollection.asPath(), document)

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
 */
suspend inline fun <reified D : Any> add(
    path: CollectionPath<D, *>,
    document: D,
): String = io.firestore4k.internal.add(
    collectionPath = path.toString(),
    document = document,
)

/**
 * Ref: https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
 */
suspend inline fun <reified D : Any, ID : Any> put(
    path: DocumentPath<D, ID>,
    document: D,
): Unit = io.firestore4k.internal.put(
    documentPath = path.toString(),
    document = document,
)

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
 */
suspend inline fun <reified D : Any, ID : Any> get(path: DocumentPath<D, ID>): D? = io.firestore4k.internal.get(
    documentPath = path.toString(),
)

suspend inline fun <reified D : Any> getAll(rootCollection: RootCollection<D, *>): Collection<D> = getAll(rootCollection.asPath())

/**
 * Ref: https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
 */
suspend inline fun <reified D : Any> getAll(
    path: CollectionPath<D, *>
): Collection<D> = io.firestore4k.internal.getAll(
    collectionPath = path.toString(),
)

/**
 * Delete is unsafe because newly added data will not be deleted.
 *
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun deleteAll(rootCollection: RootCollection<*,*>) = deleteAll(rootCollection.asPath())

/**
 * Delete is unsafe because newly added data will not be deleted.
 *
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun deleteAll(
    path: CollectionPath<*, *>
) = io.firestore4k.internal.deleteCollection(
    collectionPath = path.toString()
)

/**
 * Delete is unsafe because newly added data will not be deleted.
 *
 * Ref: https://firebase.google.com/docs/firestore/manage-data/delete-data
 */
@ExperimentalCoroutinesApi
suspend fun delete(
    path: DocumentPath<*, *>
) = io.firestore4k.internal.deleteDocument(
    documentPath = path.toString()
)
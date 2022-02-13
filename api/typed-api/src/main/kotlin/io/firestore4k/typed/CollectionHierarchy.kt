package io.firestore4k.typed

import kotlin.reflect.KClass

// sealed parent class for collection types
sealed class FirestoreCollection<E : Any, ID : Any>(
    open val id: String,
    open val type: KClass<E>,
    open val docIdType: KClass<ID>,
) {
    // this function is not defined as extension function to avoid passing duplicate generics
    inline fun <reified DOCUMENT : Any, reified DOCUMENT_ID : Any> subCollection(
        id: String
    ): SubCollection<E, ID, DOCUMENT, DOCUMENT_ID> = SubCollection(
        id = id,
        type = DOCUMENT::class,
        parent = this,
        docIdType = DOCUMENT_ID::class,
    )
}

// Root collection type
data class RootCollection<E : Any, ID : Any>(
    override val id: String,
    override val type: KClass<E>,
    override val docIdType: KClass<ID>,
) : FirestoreCollection<E, ID>(
    id = id,
    type = type,
    docIdType = docIdType,
) {
    // For path composition logic, convert [RootCollection] to [RootCollectionPath].
    fun asPath(): RootCollectionPath<E, ID> = RootCollectionPath(this)

    override fun toString(): String = id
}

// Sub collection type
data class SubCollection<PARENT : Any, PARENT_ID : Any, E : Any, ID : Any>(
    override val id: String,
    override val type: KClass<E>,
    val parent: FirestoreCollection<PARENT, PARENT_ID>,
    override val docIdType: KClass<ID>,
) : FirestoreCollection<E, ID>(
    id = id,
    type = type,
    docIdType = docIdType,
)

// helper factory method to create root collection instance
inline fun <reified DOCUMENT : Any, reified ID : Any> rootCollection(
    id: String,
): RootCollection<DOCUMENT, ID> = RootCollection(
    id = id,
    type = DOCUMENT::class,
    docIdType = ID::class,
)


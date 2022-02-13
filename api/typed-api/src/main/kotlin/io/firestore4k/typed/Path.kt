package io.firestore4k.typed

import kotlin.reflect.KClass

// sealed parent class for firestore path
sealed class FirestorePath

// 2nd level sealed parent class for collection path
sealed class CollectionPath<DOCUMENT : Any, ID : Any>(
    val collectionId: String,
    val type: KClass<DOCUMENT>,
    val docIdType: KClass<ID>,
) : FirestorePath()

// 3rd level root collection path
data class RootCollectionPath<DOCUMENT : Any, ID : Any>(
    val rootCollection: RootCollection<DOCUMENT, ID>
) : CollectionPath<DOCUMENT, ID>(
    rootCollection.id,
    rootCollection.type,
    rootCollection.docIdType
) {

    override fun toString(): String = rootCollection.id
}

// 3rd level sub collection path
data class SubCollectionPath<PARENT : Any, PARENT_ID : Any, DOCUMENT : Any, ID : Any>(
    val subCollection: SubCollection<PARENT, PARENT_ID, DOCUMENT, ID>,
    val documentPath: DocumentPath<PARENT, PARENT_ID>,
) : CollectionPath<DOCUMENT, ID>(subCollection.id, subCollection.type, subCollection.docIdType) {

    override fun toString(): String = "$documentPath/${subCollection.id}"
}

// 2nd level document path
data class DocumentPath<DOCUMENT : Any, ID : Any>(
    val collectionPath: CollectionPath<DOCUMENT, ID>,
    val documentId: ID,
) : FirestorePath() {

    val type: KClass<DOCUMENT> = collectionPath.type

    override fun toString(): String = "$collectionPath/$documentId"
}

package io.firestore4k.dynamic

sealed class FirestorePath

data class CollectionPath(
    val documentPath: DocumentPath? = null,
    val collectionId: String,
) : FirestorePath() {
    constructor(collectionId: String) : this(null, collectionId)

    override fun toString(): String = (documentPath?.toString()?.plus("/") ?: "") + collectionId
}

data class DocumentPath(
    val collectionPath: CollectionPath,
    val documentId: String,
) : FirestorePath() {

    override fun toString(): String = "$collectionPath/$documentId"
}

fun collection(id: String) = CollectionPath(collectionId = id)
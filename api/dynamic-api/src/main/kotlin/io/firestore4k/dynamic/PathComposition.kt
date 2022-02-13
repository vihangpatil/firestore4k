package io.firestore4k.dynamic

operator fun CollectionPath.div(
    documentId: String
) = DocumentPath(
    collectionPath = this,
    documentId = documentId,
)

operator fun DocumentPath.div(collectionPath: CollectionPath) = collectionPath.copy(documentPath = this)
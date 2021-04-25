package dev.vihang.firestore4k.typed

// rootCollection / ID => documentPath
// directly allow / operation of root collection as if it is a root collection path.
// Type safety for ID class by enforcing only ID instance after /
operator fun <E : Any, ID: Any> RootCollection<E, ID>.div(
    documentId: ID,
) = DocumentPath(
    collectionPath = this.asPath(),
    documentId = documentId,
)

// documentPath / subCollection => subCollectionPath
operator fun <PARENT : Any, PARENT_ID : Any, D : Any, ID : Any> DocumentPath<PARENT, PARENT_ID>.div(
    subCollection: SubCollection<PARENT, PARENT_ID, D, ID>
) = SubCollectionPath(
    documentPath = this,
    subCollection = subCollection,
)

// subCollectionPath / ID => documentPath
operator fun <PARENT : Any, PARENT_ID : Any, D : Any, ID : Any> SubCollectionPath<PARENT, PARENT_ID, D, ID>.div(
    documentId: ID
) = DocumentPath(
    collectionPath = this,
    documentId = documentId,
)

//operator fun <PARENT : Any, PARENT_ID : Any, D : Any, ID : Any> DocumentPath<PARENT, Any>.div(
//    subCollectionPath: SubCollectionPath<PARENT, PARENT_ID, D, ID>
//) = subCollectionPath.copy(documentPath = this)
package dev.vihang.firestore4k.typed


data class ClassInfo(
    val className: String,
    val packageName: String,
    val collection: Collection,
    val idClassName: String,
    val childOf: ChildOf?,
)
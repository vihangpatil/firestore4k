rootProject.name = "firestore4k"

include(
    // Flexible dynamic API with relaxed type checks for DB schema.
    "api:dynamic",
    // Typed-API with type safety for DB schema.
    "api:typed",

    // annotations used by annotation processor for typed API
    "annotations",
    // annotation processor to generate code
    "ksp",

    // internal implementation behind api
    "internal",

    // example
    "examples:org-hierarchy",
)
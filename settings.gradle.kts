plugins {
    id("de.fayard.refreshVersions") version "0.40.1"
}

refreshVersions {
    rejectVersionIf {
        candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
    }
    extraArtifactVersionKeyRules(file("refreshVersions-extra-rules.txt"))
}

rootProject.name = "firestore4k"

include(
    // Flexible dynamic API with relaxed type checks for DB schema.
    "api:dynamic-api",
    // Typed-API with type safety for DB schema.
    "api:typed-api",

    // annotations used by annotation processor for typed API
    "annotations",
    // annotation processor to generate code
    "ksp",

    // internal implementation behind api
    "internal",

    // example
    "examples:org-hierarchy",
)
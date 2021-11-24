import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Version.kotlin
    kotlin("plugin.serialization") version Version.kotlin apply false
    id("com.github.ben-manes.versions") version Version.versionsPlugin
    id("com.google.devtools.ksp") version Version.kotlinSymbolProcessing apply false
}

allprojects {
    group = "dev.vihang.firestore4k"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_16.toString()
        targetCompatibility = JavaVersion.VERSION_16.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_16.majorVersion
        }
    }
}

fun isNonStable(version: String): Boolean {
    val regex = "^[0-9,.v-]+$".toRegex()
    val isStable = regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    // Example 1: reject all non stable versions
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    // Example 2: disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }

    // Example 3: using the full syntax
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}
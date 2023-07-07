import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") apply false
    id("com.google.devtools.ksp") apply false
}

allprojects {
    group = "io.firestore4k"
    version = "2.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_20.toString()
        targetCompatibility = JavaVersion.VERSION_20.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_20.majorVersion
        }
    }
}
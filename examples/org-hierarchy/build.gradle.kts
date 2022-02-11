plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

dependencies {

    implementation(project(":api:typed-api"))
    compileOnly(project(":annotations"))
    ksp(project(":ksp"))

    implementation(Kotlin.stdlib.jdk8)

    implementation("org.slf4j:slf4j-api:_")
    implementation("org.slf4j:slf4j-simple:_")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
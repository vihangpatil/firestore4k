plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {

    implementation(project(":api:typed"))
    compileOnly(project(":annotations"))
    ksp(project(":ksp"))

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.slf4j:slf4j-api:${Version.slf4j}")
    implementation("org.slf4j:slf4j-simple:${Version.slf4j}")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
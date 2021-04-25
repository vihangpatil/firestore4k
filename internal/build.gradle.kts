plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(platform("com.google.cloud:libraries-bom:${Version.googleCloudBom}"))
    api("com.google.cloud:google-cloud-firestore")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.kotlinCoroutines}")

    api("org.slf4j:slf4j-api:${Version.slf4j}")
    runtimeOnly("org.slf4j:slf4j-simple:${Version.slf4j}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Version.jacksonKotlin}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            pom {
                name.set("Firestore4K")
                description.set("Firestore Kotlin Client with strict (and relaxed) type-system.")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("vihangpatil")
                        name.set("Vihang Patil")
                        email.set("vihang.patil@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/vihangpatil/firestore4k")
                    connection.set("scm:git:git@github.com:vihangpatil/firestore4k.git")
                    developerConnection.set("scm:git:git@github.com:vihangpatil/firestore4k.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
            val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}
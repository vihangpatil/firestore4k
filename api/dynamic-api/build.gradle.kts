plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)

    api(KotlinX.coroutines.core)
    api(KotlinX.serialization.properties)

    api(platform("com.google.cloud:libraries-bom:_"))
    api("com.google.cloud:google-cloud-firestore") {
        exclude("com.google.guava", "listenablefuture")
    }

    implementation(project(":internal"))

    testImplementation(Kotlin.test.junit5)
    testRuntimeOnly(Testing.junit.jupiter)
    testImplementation("org.testcontainers:junit-jupiter:_")
}

tasks.test {
    useJUnitPlatform()
    setEnvironment(
        "FIRESTORE_EMULATOR_HOST" to "0.0.0.0:5173",
        "PATH" to System.getenv("PATH"),
    )
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            pom {
                name.set("Firestore4K")
                description.set("Firestore Kotlin Client with strict (and relaxed) type-system.")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/vihangpatil/firestore4k")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            name = "OSSRH"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
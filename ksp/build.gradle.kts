plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation("com.google.devtools.ksp:symbol-processing-api:_")

    implementation(project(":annotations"))

    implementation(Square.kotlinPoet)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "Firestore4K"
                description = "Firestore Kotlin Client with strict (and relaxed) type-system."
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "vihangpatil"
                        name = "Vihang Patil"
                        email = "vihang.patil@gmail.com"
                    }
                }
                scm {
                    url = "https://github.com/vihangpatil/firestore4k"
                    connection = "scm:git:git@github.com:vihangpatil/firestore4k.git"
                    developerConnection = "scm:git:git@github.com:vihangpatil/firestore4k.git"
                }
            }
        }
    }
    repositories {
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
plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.dokka") version "1.9.10"
    id("application")
    id("maven-publish")
}

kotlin {
    jvmToolchain(21)
}

group = "dev.jakedoes"
version = System.getenv("VERSION")?.removePrefix("v") ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.1")
    implementation("io.ktor:ktor-network-tls:2.1.21")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(kotlin("test"))
}

application {
    mainClass = "dev.jakedoes.ApplicationKt"
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xdebug")
    }
}

tasks.named<JavaExec>("run") {
    jvmArgs("-ea")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

tasks.test {
    useJUnitPlatform()
}


tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    outputDirectory.set(layout.buildDirectory.dir("javadoc"))
}


tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}



tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")

    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(tasks.getByName<Jar>("sourcesJar"))
            artifact(tasks.getByName<Jar>("javadocJar"))

            pom {
                name.set("mumblekt")
                description.set("A Mumble wrapper, written in Kotlin")
                url.set("https://github.com/jake-does-dev/mumblekt")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("jake-does-dev")
                        name.set("Jake Allan")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/jake-does-dev/mumblekt.git")
                    developerConnection.set("scm:git:ssh://git@github.com/jake-does-dev/mumblekt.git")
                    url.set("https://github.com/jake-does-dev/mumblekt")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jake-does-dev/mumblekt")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
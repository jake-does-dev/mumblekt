plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    id("io.ktor.plugin") version "3.1.2"
    id("application")
}

kotlin {
    jvmToolchain(21)
}

group = "dev.jakedoes"
version = "1.0-SNAPSHOT"

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
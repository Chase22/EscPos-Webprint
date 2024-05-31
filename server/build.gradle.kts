plugins {
    application
    kotlin("jvm") version "2.0.0"
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.1.3")
    implementation("com.github.anastaciocintra:escpos-coffee:4.1.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
    implementation("com.bucket4j:bucket4j_jdk17-core:8.12.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("de.chasenet.MainKt")
}
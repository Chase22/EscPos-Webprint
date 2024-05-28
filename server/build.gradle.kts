plugins {
    kotlin("jvm") version "2.0.0"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.1.3")
    implementation(project(":escpos-coffee"))
    //implementation("com.github.anastaciocintra:escpos-coffee:4.1.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
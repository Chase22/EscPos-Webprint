plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "de.chasenet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.qrcode)
    implementation(libs.escposCoffee)
    implementation(libs.slf4j.api)
}

tasks.test {
    useJUnitPlatform()
}
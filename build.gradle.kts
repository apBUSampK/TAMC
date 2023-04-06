plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.kotlin.link")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("space.kscience:kmath-core:0.3.0")
    implementation("space.kscience:kmath-geometry:0.3.0")
    implementation("space.kscience:kmath-complex:0.3.0")
    implementation("space.kscience:kmath-stat:0.3.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}
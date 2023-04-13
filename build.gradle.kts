plugins {
    kotlin("jvm") version "1.8.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.kotlin.link")
}

val kmathVersion = "0.3.1-dev-RC"

dependencies {
    testImplementation(kotlin("test"))
    implementation("space.kscience:kmath-core:$kmathVersion")
    implementation("space.kscience:kmath-geometry:$kmathVersion")
    implementation("space.kscience:kmath-complex:$kmathVersion")
    implementation("space.kscience:kmath-stat:$kmathVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    target.compilations.all {
        kotlinOptions {
            freeCompilerArgs += "-jvm-target 11"
            jvmTarget = "11"
        }
    }
    sourceSets.all {
        languageSettings {

        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("MainKt")
}
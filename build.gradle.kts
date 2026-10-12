plugins {
    kotlin("jvm") version "2.4.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.rest-assured:rest-assured:6.0.1")
    testImplementation("io.rest-assured:kotlin-extensions:6.1.0")

    testImplementation(platform("com.fasterxml.jackson:jackson-bom:2.21.3"))
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
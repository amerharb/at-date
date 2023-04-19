plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.amerharb.atdate"
version = "0.0.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
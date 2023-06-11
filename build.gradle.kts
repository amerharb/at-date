plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.amerharb.atdate"
version = "0.0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":at-date-lib"))
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

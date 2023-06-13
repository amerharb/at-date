plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.amerharb.atdate"
version = "0.1.0"

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

application {
    mainClass.set("MainKt")
}

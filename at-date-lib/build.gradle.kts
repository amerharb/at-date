plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.amerharb.atdate"
version = "0.0.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.amerharb.atdate"
version = "0.1.0"


repositories {
    mavenCentral()
}

val ktor_version = "2.3.1"
dependencies {
    implementation(project(":at-date-lib"))
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.2.9")
}

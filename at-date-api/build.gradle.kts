plugins {
	kotlin("jvm") version "1.8.0"
}

val ktor_version:String by project
val logback_version:String by project

group = "com.amerharb.atdate"
version = "0.1.1"

dependencies {
	implementation(project(":at-date-lib"))
	implementation("io.ktor:ktor-server-netty:$ktor_version")
	implementation("io.ktor:ktor-server-core:$ktor_version")
	implementation("ch.qos.logback:logback-classic:$logback_version")
}

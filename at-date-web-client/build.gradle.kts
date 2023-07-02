val ktor_version: String = "2.3.1" // by project.rootProject.ext
val logback_version: String = "1.2.11" // by project.rootProject.ext

plugins {
	kotlin("jvm") version "1.8.0"
	id("io.ktor.plugin") version "2.3.1"
}

group = "com.amerharb.atdate"
version = "0.1.0"
application {
	mainClass.set("com.amerharb.ApplicationKt")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
	implementation("ch.qos.logback:logback-classic:$logback_version")
	testImplementation(kotlin("test"))
	testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
}

tasks {
	// fake task to fix gradle problem
	register("prepareKotlinBuildScriptModel") {
	}
}

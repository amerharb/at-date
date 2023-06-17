plugins {
	kotlin("jvm") version "1.8.0"
	id("org.jlleitschuh.gradle.ktlint") version "11.4.0"
}

group = "com.amerharb.atdate"
version = "0.1.0"

repositories {
	mavenCentral()
}

ktlint {
	verbose.set(true)
	outputToConsole.set(true)
	coloredOutput.set(true)
}

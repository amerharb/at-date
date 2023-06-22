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
ktlint {
	version.set("0.49.1")
	verbose.set(true)
	outputToConsole.set(true)
	coloredOutput.set(true)
}

subprojects {
	apply(plugin = "org.jlleitschuh.gradle.ktlint")

	repositories {
		mavenCentral()
	}

	configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
		debug.set(true)
	}
}

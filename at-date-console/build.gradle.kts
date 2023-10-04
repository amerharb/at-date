plugins {
	kotlin("jvm") version "1.8.0"
	application
}

group = "com.amerharb.atdate"
version = "0.2.0"

dependencies {
	implementation(project(":at-date-lib"))
	testImplementation(kotlin("test"))
}

application {
	mainClass.set("MainKt")
}

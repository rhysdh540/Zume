import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	id("cosmicloom") version("1.1.1")
}

dependencies {
	cosmicReach(loom.cosmicReachClient("pre-alpha", "0.3.14"))
	modImplementation(loom.cosmicQuilt("2.3.2"))
	implementation(project(":api"))
}

val outputJar by tasks.creating(ShadowJar::class) {
	archiveClassifier = "output"
	from(tasks.jar)
}
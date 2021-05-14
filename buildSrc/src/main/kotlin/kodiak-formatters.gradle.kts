plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

description = "Kodiak - Javadoc Models"

java {
    withSourcesJar()
}

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("io.github.copper-leaf:clog-core:4.1.1")
    api("io.github.copper-leaf:common-core:3.0.0")

    api(project(":common:common-formatter"))

    testApi(kotlin("test-common"))
    testApi(kotlin("test-junit"))
    testApi(kotlin("test-annotations-common"))
}

tasks.withType<JavaCompile> {
    sourceCompatibility = Config.javaVersion
    targetCompatibility = Config.javaVersion
}
tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.useIR = true
    kotlinOptions {
        jvmTarget = Config.javaVersion
    }
}

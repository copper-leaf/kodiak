plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `copper-leaf-publish`
}

description = "Kodiak - Common Formatter"

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("io.github.copper-leaf:clog-core:4.1.1")

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

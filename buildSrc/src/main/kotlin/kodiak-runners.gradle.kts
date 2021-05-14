plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("io.github.copper-leaf:clog-core:4.1.1")

    api(project(":common:common-models"))
    api(project(":common:common-runner"))

    testApi(kotlin("test-common"))
    testApi(kotlin("test-junit"))
    testApi(kotlin("test-annotations-common"))
    testApi("io.github.copper-leaf:common-core:3.0.0")
}

java {
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = Config.javaVersion
    targetCompatibility = Config.javaVersion
}
tasks.withType<Test> {
    outputs.upToDateWhen { false }
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
sourceSets {
    create("example")
}

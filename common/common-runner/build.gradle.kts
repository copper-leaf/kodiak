plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `copper-leaf-publish`
}

description = "Kodiak - Common Runner"

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("io.github.copper-leaf:clog-core:4.1.1")

    api(project(":common:common-models"))

    testApi(kotlin("test-common"))
    testApi(kotlin("test-junit"))
    testApi(kotlin("test-annotations-common"))
}

java {
    withSourcesJar()
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

val updateVersionLine by tasks.registering {
    doLast {
        val regex = """
        |// -- VERSION --
        |const val version = ".*?"
        |// -- ENDVERSION --
        """.trimMargin().toRegex()

        val replacement = """
        |// -- VERSION --
        |const val version = "${project.version}"
        |// -- ENDVERSION --
        """.trimMargin()

        val utilsFile = file("${project.projectDir}/src/main/kotlin/utils.kt")
        utilsFile.writeText(
            utilsFile.readText().replace(regex, replacement)
        )
    }
}
val compileJava by tasks
compileJava.dependsOn(updateVersionLine)

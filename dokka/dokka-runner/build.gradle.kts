plugins {
    id("org.jetbrains.dokka") version "0.9.18"
}

apply(from = "${rootProject.rootDir}/gradle/groups/runners.gradle")

dependencies {
    "compile"(project(":common:common-runner"))
    "compile"(project(":dokka:dokka-models"))
}

// wait to build this project until after the formatter is built, because we want to bundle the formatter as a resource
// in this project
val formatterProject = project(":dokka:dokka-formatter")

val thisProjectAssemble = tasks.getByName("assemble")
val formatterProjectAssemble = formatterProject.tasks.getByName("assemble")
thisProjectAssemble.dependsOn(formatterProjectAssemble)

(tasks.getByName("processResources") as ProcessResources).apply {
    from("${formatterProject.buildDir}/libs/dokka-formatter-$version-all.zip")
}

(tasks.getByName("dokka") as org.jetbrains.dokka.gradle.DokkaTask).apply {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka/compare"

    sourceDirs = files("src/example/kotlin")
}

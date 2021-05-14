plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-runners`
    `copper-leaf-publish`
    id("org.jetbrains.dokka") version "0.9.18"
}

description = "Kodiak - Dokka Runner"

dependencies {
    "compile"(project(":common:common-runner"))
    "compile"(project(":dokka:dokka-models"))
}

// wait to build this project until after the formatter is built, because we want to bundle the formatter as a resource
// in this project
val formatterProject = project(":dokka:dokka-formatter")
val formatterProjectShadowJar by formatterProject.tasks.named("shadowJar")

tasks.withType<ProcessResources> {
    dependsOn(formatterProjectShadowJar)
    from("${formatterProject.buildDir}/libs/dokka-formatter-$version-all.zip")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka/compare"

    sourceDirs = files("src/example/kotlin")
}

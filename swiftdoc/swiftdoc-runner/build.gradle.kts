plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-runners`
    `copper-leaf-publish`
}

description = "Kodiak - Swiftdoc Runner"

dependencies {
    api(project(":common:common-runner"))
    api(project(":swiftdoc:swiftdoc-models"))
}

// wait to build this project until after the formatter is built, because we want to bundle the formatter as a resource
// in this project
val formatterProject = project(":swiftdoc:swiftdoc-formatter")
val formatterProjectShadowJar by formatterProject.tasks.named("shadowJar")

tasks.withType<ProcessResources> {
    dependsOn(formatterProjectShadowJar)
    from("${formatterProject.buildDir}/libs/swiftdoc-formatter-$version-all.zip")
}

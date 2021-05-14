plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-runners`
    `copper-leaf-publish`
}

description = "Kodiak - Javadoc Runner"

dependencies {
    api(project(":common:common-runner"))
    api(project(":javadoc:javadoc-models"))
}

// wait to build this project until after the formatter is built, because we want to bundle the formatter as a resource
// in this project
val formatterProject = project(":javadoc:javadoc-formatter")
val formatterProjectShadowJar by formatterProject.tasks.named("shadowJar")

tasks.withType<ProcessResources> {
    dependsOn(formatterProjectShadowJar)
    from("${formatterProject.buildDir}/libs/javadoc-formatter-$version-all.zip")
}

tasks.withType<Javadoc> {
    source = fileTree("$projectDir/src/example/java")
    setDestinationDir(file("$buildDir/javadoc/example"))
}

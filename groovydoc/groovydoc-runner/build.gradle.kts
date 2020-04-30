apply(from = "${rootProject.rootDir}/gradle/groups/runners.gradle")

dependencies {
    "compile"(project(":common:common-runner"))
    "compile"(project(":groovydoc:groovydoc-models"))
}

// wait to build this project until after the formatter is built, because we want to bundle the formatter as a resource
// in this project
val formatterProject = project(":groovydoc:groovydoc-formatter")

val thisProjectAssemble = tasks.getByName("assemble")
val formatterProjectAssemble = formatterProject.tasks.getByName("assemble")
thisProjectAssemble.dependsOn(formatterProjectAssemble)

(tasks.getByName("processResources") as ProcessResources).apply {
    from("${formatterProject.buildDir}/libs/groovydoc-formatter-$version-all.zip")
}

apply(from = "${rootProject.rootDir}/gradle/groups/formatters.gradle")
apply(from = "${rootProject.rootDir}/gradle/actions/shadow.gradle")

dependencies {
    "implementation"(project(":common:common-formatter"))
    "implementation"(project(":dokka:dokka-models"))

    // use locally-compiled version of Dokka with the `<ERROR CLASS>` bug fixed, until it"s published
    "implementation"(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

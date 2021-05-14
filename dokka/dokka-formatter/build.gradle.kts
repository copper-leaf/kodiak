plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-formatters`
    `copper-leaf-publish`
    `copper-leaf-shadow`
}

description = "Kodiak - Dokka Formatter"

dependencies {
    api(project(":dokka:dokka-models"))

    // use locally-compiled version of Dokka with the `<ERROR CLASS>` bug fixed, until it"s published
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

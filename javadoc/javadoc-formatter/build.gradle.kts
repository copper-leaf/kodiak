plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-formatters`
    `copper-leaf-publish`
    `copper-leaf-shadow`
}

description = "Kodiak - Javadoc Formatter"

dependencies {
    api(project(":javadoc:javadoc-models"))

    val toolsJar = org.gradle.internal.jvm.Jvm.current().toolsJar
    if (toolsJar != null) {
        implementation(files(toolsJar))
        shadow(files(toolsJar))
    }
}

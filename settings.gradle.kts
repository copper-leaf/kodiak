pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "binary-compatibility-validator" -> useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
            }
        }
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://kotlin.bintray.com/kotlinx")
    }
}

rootProject.name = "kodiak"

include(":docs")

include(":common:common-models")
include(":common:common-formatter")
include(":common:common-runner")

include(":dokka:dokka-models")
include(":dokka:dokka-formatter")
include(":dokka:dokka-runner")

include(":groovydoc:groovydoc-models")
include(":groovydoc:groovydoc-formatter")
include(":groovydoc:groovydoc-runner")

include(":javadoc:javadoc-models")
include(":javadoc:javadoc-formatter")
include(":javadoc:javadoc-runner")

include(":swiftdoc:swiftdoc-models")
include(":swiftdoc:swiftdoc-formatter")
include(":swiftdoc:swiftdoc-runner")

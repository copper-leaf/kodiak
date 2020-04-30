package com.copperleaf.kodiak.kotlin

fun main() {

    org.jetbrains.dokka.MainKt.main(
        arrayOf(
            "-pass",
            "-format", "json",
            "-noStdlibLink",
            "-analysisPlatform", "JVM",
            "-src", "/Users/cbrooks/Documents/personal/kodiak/javadoc/javadoc-runner/src/example/java",
            "-src", "/Users/cbrooks/Documents/personal/kodiak/dokka/dokka-runner/src/example/kotlin",
            "-output",
            "/Users/cbrooks/Documents/personal/kodiak/dokka/dokka-runner/build/kodiak/output"
        )
    )
}

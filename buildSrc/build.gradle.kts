plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:4.1.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.0.0")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.4.32")
    implementation("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    implementation("de.undercouch:gradle-download-task:4.0.2")
}

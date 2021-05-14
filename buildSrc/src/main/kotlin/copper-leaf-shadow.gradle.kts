import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

val shadowJar by tasks
val assemble by tasks

assemble.dependsOn(shadowJar)

tasks.withType<ShadowJar> {
    archiveVersion.set(project.version.toString())
    archiveExtension.set("zip")
}

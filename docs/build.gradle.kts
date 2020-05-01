plugins {
    id("com.eden.orchidPlugin") version "0.20.0"
}

group = rootProject.group
version = rootProject.version

repositories {
    jcenter()
}

dependencies {
    orchidRuntimeOnly(Libs.orchiddocs)
    orchidRuntimeOnly(Libs.orchidcopper)
    orchidRuntimeOnly(Libs.orchidplugindocs)
    orchidRuntimeOnly(Libs.orchidgithub)
    orchidRuntimeOnly(Libs.orchidkotlindoc)
}

// Orchid config
//----------------------------------------------------------------------------------------------------------------------

orchid {
    githubToken = if (project.hasProperty("github_token"))
        project.property("github_token")?.toString()
    else
        System.getenv("GITHUB_TOKEN")?.toString()

    args = listOf("--experimentalSourceDoc")
}
val orchidBuild by tasks
val orchidDeploy by tasks

//val build by tasks
//build.dependsOn(orchidBuild)

val publish by tasks.registering {
    dependsOn(orchidDeploy)
}

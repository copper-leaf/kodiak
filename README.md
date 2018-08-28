# dokka-json
Dokka formatter to output JSON

## Usage

`build.gradle`

```groovy
configurations { dokka }
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    dokka 'org.jetbrains.dokka:dokka-fatjar:0.9.17'
	  dokka 'com.github.copper-leaf:dokka-json:0.1.0'
}
task runDokkaWithJsonFormatter(type: JavaExec) {
    dependsOn classes
    outputs.files(fileTree("${project.buildDir}/docs/dokkaJson"))

    main = "org.jetbrains.dokka.MainKt"
    classpath = configurations.dokka
    args = [
            "-format", "json",
            "-src", "${project.projectDir}/src/main/kotlin",
            "-output", "${project.buildDir}/docs/dokkaJson"
    ]
}
project.tasks.assemble.dependsOn runDokkaWithJsonFormatter
```

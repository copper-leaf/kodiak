/**
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val com_eden_orchidplugin_gradle_plugin: String = "0.17.1" 

    const val clog4j: String = "2.0.7" 

    const val com_eden_common: String = "1.11.4" 

    const val shadow: String = "5.0.0" 

    const val gradle_bintray_plugin: String = "1.8.4" 

    const val buildsrcversions: String = "0.3.2" 

    const val io_github_javaeden_orchid: String = "0.17.1" 

    const val strikt_core: String = "0.20.1" 

    const val groovy_all: String = "2.5.7" 

    const val dokka_fatjar: String = "0.9.18"

    const val org_jetbrains_kotlin: String = "1.3.31" 

    const val kotlinx_serialization_runtime: String = "0.11.0" 
            // available: "0.11.1-1.3.40-eap-107"

    const val jsoup: String = "1.12.1" 

    const val org_junit_jupiter: String = "5.4.2" 

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "5.4.1"

        const val currentVersion: String = "5.4.1"

        const val nightlyVersion: String = "5.6-20190615000035+0000"

        const val releaseCandidate: String = "5.5-rc-3"
    }
}

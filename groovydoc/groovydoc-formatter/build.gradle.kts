apply(from = "${rootProject.rootDir}/gradle/groups/formatters.gradle")
apply(from = "${rootProject.rootDir}/gradle/actions/shadow.gradle")

dependencies {
    "implementation"(project(":common:common-formatter"))
    "implementation"(project(":groovydoc:groovydoc-models"))
    
    "implementation"(Libs.groovy_all)
    "implementation"(Libs.jsoup)
}

plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `kodiak-formatters`
    `copper-leaf-publish`
    `copper-leaf-shadow`
}

description = "Kodiak - Groovydoc Formatter"

dependencies {
    api(project(":groovydoc:groovydoc-models"))
    api("org.codehaus.groovy:groovy-all:2.5.8")
    api("org.jsoup:jsoup:1.12.1")
}

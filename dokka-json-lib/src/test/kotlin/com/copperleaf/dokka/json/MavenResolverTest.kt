package com.copperleaf.dokka.json

import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.any
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import strikt.assertions.map
import java.nio.file.Files
import java.nio.file.Path

class MavenResolverTest {

    lateinit var client: OkHttpClient
    lateinit var cacheDir: Path
    lateinit var resolver: MavenResolver

    @BeforeEach
    internal fun setUp() {
        client = OkHttpClient.Builder().build()
        cacheDir = Files.createTempDirectory("LibTest")
        resolver = MavenResolverImpl(client, cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        cacheDir.toFile().deleteRecursively()
    }

    @Test
    fun testResolveSingleJar() {
        val inputTarget = "javax.inject:javax.inject:1"

        val output = resolver.getMavenJars(inputTarget)

        expectThat(output)
                .isNotEmpty()
                .hasSize(1)[0]
                .chain { it.jarPath }
                .isNotNull()
                .chain { it.toFile().absolutePath }
                .isEqualTo("${cacheDir.toFile().absolutePath}/javax/inject/javax.inject/1/javax.inject-1.jar")
    }

    @Test
    fun testResolveJarWithDependencies() {
        val inputTarget = "com.eden:Clog:2.0.4"

        val output = resolver.getMavenJars(inputTarget)

        expectThat(output)
                .isNotEmpty()
                .hasSize(2)
                .map { it.jarPath!!.toFile().absolutePath }
                .any { isEqualTo("${cacheDir.toFile().absolutePath}/com/eden/Clog/2.0.4/Clog-2.0.4.jar") }
                .any { isEqualTo("${cacheDir.toFile().absolutePath}/org/fusesource/jansi/jansi/1.14/jansi-1.14.jar") }
    }

}
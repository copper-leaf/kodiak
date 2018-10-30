package com.copperleaf.javadoc.json

import com.copperleaf.dokka.json.Artifact
import com.copperleaf.dokka.json.MavenResolver
import com.copperleaf.dokka.json.MavenResolverImpl
import kotlinx.io.InputStream
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class JavadocJsonRunnerTest {

    lateinit var client: OkHttpClient
    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var resolver: MavenResolver
    lateinit var javadocRunner: JavadocdocInvoker

    private val useTempDirs = false

    private fun initTempDirs() {
        cacheDir = Files.createTempDirectory("JavadocJsonRunnerTest")
        outputDir = Files.createTempDirectory("javadocTestOutput")
    }
    private fun cleanupTempDirs() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    private fun initProjectDirs() {
        cacheDir = File("../javadoc-json/build/javadoc/cache").canonicalFile.toPath()
        outputDir = File("../javadoc-json/build/javadoc/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }
    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        client = OkHttpClient.Builder().build()

        if(useTempDirs) initTempDirs() else initProjectDirs()

        resolver = MavenResolverImpl(client, cacheDir)
        javadocRunner = JavadocdocInvokerImpl(resolver, outputDir, listOf(
                Artifact.from("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.60"),
                Artifact.from("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.6.1"),

                Artifact.from("copper-leaf", "javadoc-json",        "0.1.0", File("../javadoc-json/build/libs/javadoc-json-0.1.0.jar").canonicalFile.toPath()),
                Artifact.from("copper-leaf", "javadoc-json-models", "0.1.0", File("../javadoc-json-models/build/libs/javadoc-json-models-0.1.0.jar").canonicalFile.toPath())
        ))
    }

    @AfterEach
    internal fun tearDown() {
        if(useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningJavadoc() {
        try {
            val rootDoc = javadocRunner.getRootDoc(
                    listOf(
                            File("../javadoc-json/src/test/java").canonicalFile.toPath()
                    )
            ) { inputStream -> InputStreamPrinter(inputStream) }

            expectThat(rootDoc)
                    .isNotNull()
                    .and { chain { it.packages }.isNotEmpty() }
                    .and { chain { it.classes  }.isNotEmpty() }
        }
        catch(t: Throwable) {
            println(t.message)
            throw t
        }
    }

}

class InputStreamPrinter(private val inputStream: InputStream) : Runnable {
    override fun run() {
        BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8"))).lines().forEach {
            println("[tag] $it")
        }
    }
}
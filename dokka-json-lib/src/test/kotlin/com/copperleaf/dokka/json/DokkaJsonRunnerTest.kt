package com.copperleaf.dokka.json

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

class DokkaJsonRunnerTest {

    lateinit var client: OkHttpClient
    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var resolver: MavenResolver
    lateinit var dokkaRunner: KotlindocInvoker

    private val useTempDirs = false

    private fun initTempDirs() {
        cacheDir = Files.createTempDirectory("DokkaJsonRunnerTest")
        outputDir = Files.createTempDirectory("dokkaTestOutput")
    }
    private fun cleanupTempDirs() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    private fun initProjectDirs() {
        cacheDir = File("../dokka-json/build/dokka/cache").canonicalFile.toPath()
        outputDir = File("../dokka-json/build/dokka/output").canonicalFile.toPath()
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
        dokkaRunner = KotlindocInvokerImpl(resolver, outputDir, listOf(
                Artifact.from("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.60"),
                Artifact.from("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.6.1"),
                Artifact.from("org.jetbrains.dokka:dokka-fatjar:0.9.17"),

                Artifact.from("copper-leaf", "dokka-json",        "0.1.0", File("../dokka-json/build/libs/dokka-json-0.1.0.jar").canonicalFile.toPath()),
                Artifact.from("copper-leaf", "dokka-json-models", "0.1.0", File("../dokka-json-models/build/libs/dokka-json-models-0.1.0.jar").canonicalFile.toPath())
        ))
    }

    @AfterEach
    internal fun tearDown() {
        if(useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningDokka() {
        try {
            val rootDoc = dokkaRunner.getRootDoc(
                    listOf(
                            File("../dokka-json/src/test/java").canonicalFile.toPath(),
                            File("../dokka-json/src/test/kotlin").canonicalFile.toPath()
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
            println(it)
        }
    }
}
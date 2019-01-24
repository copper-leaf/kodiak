package com.copperleaf.dokka.json

import kotlinx.io.InputStream
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

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
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
        cacheDir = File("../dokka-formatter/build/dokka/cache").canonicalFile.toPath()
        outputDir = File("../dokka-formatter/build/dokka/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        dokkaRunner = KotlindocInvokerImpl(cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningDokka() {
        try {
            val rootDoc = dokkaRunner.getRootDoc(
                listOf(
                    File("../dokka-formatter/src/test/java").canonicalFile.toPath(),
                    File("../dokka-formatter/src/test/kotlin").canonicalFile.toPath()
                ),
                outputDir
            ) { inputStream -> InputStreamPrinter(inputStream) }

            expectThat(rootDoc)
                .isNotNull()
                .and { chain { it.packages }.isNotEmpty() }
                .and { chain { it.classes }.isNotEmpty() }
        } catch (t: Throwable) {
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
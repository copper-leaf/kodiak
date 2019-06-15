package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinModuleDoc
import com.copperleaf.json.common.DocInvoker
import com.eden.common.util.IOStreamUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class DokkaJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var dokkaRunner: DocInvoker<KotlinModuleDoc>

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
        cacheDir = File("../dokka-runner/build/dokka/cache").canonicalFile.toPath()
        outputDir = File("../dokka-runner/build/dokka/output").canonicalFile.toPath()
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
        val rootDoc = dokkaRunner.getModuleDoc(
            listOf(
                File("../dokka-runner/src/example/java").canonicalFile.toPath(),
                File("../dokka-runner/src/example/kotlin").canonicalFile.toPath()
            ),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        expectThat(rootDoc)
            .isNotNull()
            .and { chain { it.packages }.isNotEmpty() }
            .and { chain { it.classes }.isNotEmpty() }
    }

}

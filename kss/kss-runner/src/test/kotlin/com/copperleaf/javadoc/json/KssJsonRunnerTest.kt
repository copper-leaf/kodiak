package com.copperleaf.kss.json

import com.eden.common.util.IOStreamUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class KssJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var kssRunner: KssInvoker

    private val useTempDirs = false

    private fun initTempDirs() {
        cacheDir = Files.createTempDirectory("KssJsonRunnerTest")
        outputDir = Files.createTempDirectory("kssTestOutput")
    }

    private fun cleanupTempDirs() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    private fun initProjectDirs() {
        cacheDir = File("../kss-formatter/build/kss/cache").canonicalFile.toPath()
        outputDir = File("../kss-formatter/build/kss/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        kssRunner = KssInvokerImpl(cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningKss() {
        try {
            val rootDoc = kssRunner.getRootDoc(
                listOf(File("../kss-formatter/src/test/java").canonicalFile.toPath()),
                outputDir
            ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

            expectThat(rootDoc)
                .isNotNull()
        } catch (t: Throwable) {
            println(t.message)
            throw t
        }
    }

}

package com.copperleaf.kodiak.kotlin

import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.kotlin.models.KotlinModuleDoc
import com.eden.common.util.IOStreamUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        cacheDir = File("../dokka-runner/build/kodiak/cache").canonicalFile.toPath()
        outputDir = File("../dokka-runner/build/kodiak/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {
    }

    @Before
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        dokkaRunner = KotlindocInvokerImpl(cacheDir)
    }

    @After
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningDokka() {
        val rootDoc = dokkaRunner.getModuleDoc(
            listOf(
                File("../../javadoc/javadoc-runner/src/example/java").canonicalFile.toPath(),
                File("../dokka-runner/src/example/kotlin").canonicalFile.toPath()
            ),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        assertNotNull(rootDoc)
        assertTrue(rootDoc.packages.isNotEmpty())
        assertTrue(rootDoc.classes.isNotEmpty())
    }
}

package com.copperleaf.kodiak.groovy

import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.groovy.models.GroovyModuleDoc
import com.eden.common.util.IOStreamUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GroovydocJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var groovydocRunner: DocInvoker<GroovyModuleDoc>

    private val useTempDirs = false

    private fun initTempDirs() {
        cacheDir = Files.createTempDirectory("groovydocJsonRunnerTest")
        outputDir = Files.createTempDirectory("groovydocTestOutput")
    }

    private fun cleanupTempDirs() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    private fun initProjectDirs() {
        cacheDir = File("../groovydoc-runner/build/kodiak/cache").canonicalFile.toPath()
        outputDir = File("../groovydoc-runner/build/kodiak/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {
    }

    @Before
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        groovydocRunner = GroovydocInvokerImpl(cacheDir)
    }

    @After
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningGroovydoc() {
        val rootDoc = groovydocRunner.getModuleDoc(
            listOf(
                File("../../javadoc/javadoc-runner/src/example/java").canonicalFile.toPath(),
                File("../groovydoc-runner/src/example/groovy").canonicalFile.toPath()
            ),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        assertNotNull(rootDoc)
        assertTrue(rootDoc.packages.isNotEmpty())
        assertTrue(rootDoc.classes.isNotEmpty())
    }
}

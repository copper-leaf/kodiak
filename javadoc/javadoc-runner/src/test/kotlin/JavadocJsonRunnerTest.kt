package com.copperleaf.kodiak.java

import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.java.models.JavaModuleDoc
import com.eden.common.util.IOStreamUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JavadocJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var javadocRunner: DocInvoker<JavaModuleDoc>

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
        cacheDir = File("../javadoc-runner/build/kodiak/cache").canonicalFile.toPath()
        outputDir = File("../javadoc-runner/build/kodiak/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {
    }

    @Before
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        javadocRunner = JavadocInvokerImpl(cacheDir)
    }

    @After
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningJavadoc() {
        val rootDoc = javadocRunner.getModuleDoc(
            listOf(File("../javadoc-runner/src/example/java").canonicalFile.toPath()),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        assertNotNull(rootDoc)
        assertTrue(rootDoc.packages.isNotEmpty())
        assertTrue(rootDoc.classes.isNotEmpty())
    }
}

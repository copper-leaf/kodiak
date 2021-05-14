package com.copperleaf.kodiak.swift

import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.swift.models.SwiftModuleDoc
import com.eden.common.util.IOStreamUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SwiftdocJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var swiftdocRunner: DocInvoker<SwiftModuleDoc>

    private val useTempDirs = false

    private fun initTempDirs() {
        cacheDir = Files.createTempDirectory("SwiftdocJsonRunnerTest")
        outputDir = Files.createTempDirectory("swiftdocTestOutput")
    }

    private fun cleanupTempDirs() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    private fun initProjectDirs() {
        cacheDir = File("../swiftdoc-runner/build/kodiak/cache").canonicalFile.toPath()
        outputDir = File("../swiftdoc-runner/build/kodiak/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {
    }

    @Before
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        swiftdocRunner = SwiftdocInvokerImpl(cacheDir)
    }

    @After
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningSwiftdoc() {
        val rootDoc = swiftdocRunner.getModuleDoc(
            listOf(File("src/example/swift").canonicalFile.toPath()),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        assertNotNull(rootDoc)
        assertTrue(rootDoc.sourceFiles.isNotEmpty())
        assertTrue(rootDoc.classes.isNotEmpty())
    }
}

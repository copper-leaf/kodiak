package com.copperleaf.kodiak.swift

import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.swift.models.SwiftModuleDoc
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
        cacheDir = File("build/docs/cache").canonicalFile.toPath()
        outputDir = File("build/docs/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        swiftdocRunner = SwiftdocInvokerImpl(cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningSwiftdoc() {
        val rootDoc = swiftdocRunner.getModuleDoc(
            listOf(File("src/example/swift").canonicalFile.toPath()),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        expectThat(rootDoc)
            .isNotNull()
            .and { get { sourceFiles }.isNotEmpty() }
            .and { get { classes }.isNotEmpty() }
    }

}

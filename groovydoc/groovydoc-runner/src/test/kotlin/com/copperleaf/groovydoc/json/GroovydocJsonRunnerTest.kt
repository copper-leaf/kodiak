package com.copperleaf.groovydoc.json

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

class GroovydocJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var groovydocRunner: GroovydocInvoker

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
        cacheDir = File("../groovydoc-formatter/build/groovydoc/cache").canonicalFile.toPath()
        outputDir = File("../groovydoc-formatter/build/groovydoc/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        groovydocRunner = GroovydocInvokerImpl(cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningSwiftdoc() {
        try {
            val rootDoc = groovydocRunner.getRootDoc(
                listOf(
                    File("../groovydoc-formatter/src/test/java").canonicalFile.toPath(),
                    File("../groovydoc-formatter/src/test/groovy").canonicalFile.toPath()
                ),
                outputDir
            ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

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

package com.copperleaf.groovydoc.json

import com.copperleaf.groovydoc.json.models.GroovyModuleDoc
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
        cacheDir = File("../groovydoc-runner/build/groovydoc/cache").canonicalFile.toPath()
        outputDir = File("../groovydoc-runner/build/groovydoc/output").canonicalFile.toPath()
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
    fun testRunningGroovydoc() {
        try {
            val rootDoc = groovydocRunner.getModuleDoc(
                listOf(
                    File("../groovydoc-runner/src/example/java").canonicalFile.toPath(),
                    File("../groovydoc-runner/src/example/groovy").canonicalFile.toPath()
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

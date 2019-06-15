package com.copperleaf.javadoc.json

import com.copperleaf.javadoc.json.models.JavaRootDoc
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

class JavadocJsonRunnerTest {

    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var javadocRunner: DocInvoker<JavaRootDoc>

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
        cacheDir = File("../javadoc-runner/build/javadoc/cache").canonicalFile.toPath()
        outputDir = File("../javadoc-runner/build/javadoc/output").canonicalFile.toPath()
        outputDir.toFile().deleteRecursively()
        outputDir.toFile().mkdirs()
    }

    private fun cleanupProjectDirs() {

    }

    @BeforeEach
    internal fun setUp() {
        if (useTempDirs) initTempDirs() else initProjectDirs()
        javadocRunner = JavadocInvokerImpl(cacheDir)
    }

    @AfterEach
    internal fun tearDown() {
        if (useTempDirs) cleanupTempDirs() else cleanupProjectDirs()
    }

    @Test
    fun testRunningJavadoc() {
        val rootDoc = javadocRunner.getModuleDoc(
            listOf(File("../javadoc-runner/src/example/java").canonicalFile.toPath()),
            outputDir
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }

        expectThat(rootDoc)
            .isNotNull()
            .and { chain { it.packages }.isNotEmpty() }
            .and { chain { it.classes }.isNotEmpty() }
    }

}

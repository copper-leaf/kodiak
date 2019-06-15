package com.copperleaf.json.common

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

abstract class BaseDocInvoker<T : ModuleDoc>(
    private val cacheDir: Path
) : DocInvoker<T> {

    protected abstract val formatterJarName: String
    protected abstract fun loadModuleDocFromDisk(destinationDir: Path): T
    protected abstract fun createProcessArgs(
        sourceDirs: List<Path>,
        destinationDir: Path,
        args: List<String>
    ): Array<String>

    protected val formatterJar: Path by lazy { cacheDir.resolve("$formatterJarName.jar") }

    final override fun getModuleDoc(
        sourceDirs: List<Path>,
        destinationDir: Path,
        cliArgs: List<String>,
        callback: (InputStream) -> Runnable
    ): T? {
        val success = execute(sourceDirs, destinationDir, cliArgs) { callback(it) }
        return if (success) loadModuleDocFromDisk(destinationDir) else null
    }

    final override fun loadCachedModuleDoc(destinationDir: Path): T? {
        return if (Files.exists(destinationDir) && destinationDir.toFile().list().isNotEmpty()) {
            loadModuleDocFromDisk(destinationDir)
        } else {
            null
        }
    }

// Run documentation runner
//----------------------------------------------------------------------------------------------------------------------

    private fun cacheEmbeddedJar() {
        formatterJar.parent.toFile().mkdirs()
        Files.copy(
            this.javaClass.getResourceAsStream("/$formatterJarName.zip"),
            formatterJar,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun execute(
        sourceDirs: List<Path>,
        destinationDir: Path,
        args: List<String>,
        callback: (InputStream) -> Runnable
    ): Boolean {
        cacheEmbeddedJar()
        val process = ProcessBuilder()
            .command(*createProcessArgs(sourceDirs, destinationDir, args))
            .redirectErrorStream(true)
            .start()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(callback(process.inputStream))
        val exitValue = process.waitFor()
        executor.shutdown()
        return exitValue == 0
    }

// Helper Functions
//----------------------------------------------------------------------------------------------------------------------

    @UseExperimental(UnstableDefault::class)
    protected fun <T : Any> getDocsInSubdirectory(
        destinationDir: Path,
        subdirectory: String,
        deserializer: DeserializationStrategy<T>
    ): List<T> {
        return destinationDir
            .resolve(subdirectory)
            .toFile()
            .walkTopDown()
            .filter { it.isFile }
            .filter { it.exists() }
            .mapNotNull { Json.parse(deserializer, it.readText()) }
            .toList()
    }

}

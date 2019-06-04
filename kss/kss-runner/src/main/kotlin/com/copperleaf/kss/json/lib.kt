package com.copperleaf.kss.json

import com.copperleaf.javadoc.json.models.KssRootdoc
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Executors

interface KssInvoker {
    fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String> = emptyList(),
            callback: (InputStream) -> Runnable
    ): KssRootdoc?

    fun loadCachedRootDoc(destinationDir: Path): KssRootdoc?
}

class KssInvokerImpl(
        private val cacheDir: Path = Files.createTempDirectory("kss-runner"),
        private val startMemory: String = "256m",
        private val maxMemory: String = "1024m"
) : KssInvoker {

    val formatterJar = cacheDir.resolve("kss-formatter.jar")

    override fun getRootDoc(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): KssRootdoc? {
        val success = executeKss(sourceDirs, destinationDir, args) { callback(it) }
        return if (success) getKssRootdoc(destinationDir) else null
    }

    override fun loadCachedRootDoc(destinationDir: Path): KssRootdoc? {
        return if (Files.exists(destinationDir) && destinationDir.toFile().list().isNotEmpty()) {
            getKssRootdoc(destinationDir)
        }
        else {
            null
        }
    }

// Run Kss
//----------------------------------------------------------------------------------------------------------------------

    private fun cacheEmbeddedJar() {
        formatterJar.parent.toFile().mkdirs()
        Files.copy(
            this.javaClass.getResourceAsStream("/kss-formatter-all.zip"),
            formatterJar,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    private fun executeKss(
            sourceDirs: List<Path>,
            destinationDir: Path,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ): Boolean {
        cacheEmbeddedJar()

        val allFiles = mutableListOf<String>()
        sourceDirs.forEach {
            it.toFile().walk().forEach { f ->
                if (f.isFile && f.extension == "java") {
                    allFiles.add(f.absolutePath)
                }
            }
        }

        val processArgs = arrayOf(
            "java",
            "-Xms$startMemory", "-Xmx$maxMemory",
            "-classpath", formatterJar.toFile().absolutePath, // classpath of embedded formatter jar
            "com.copperleaf.kss.json.MainKt", // Groovydoc Formatter main class
            "--src", sourceDirs.map { it.toFile().absolutePath }.joinToString(separator = File.pathSeparator), // the sources to process
            "--output", destinationDir.toFile().absolutePath, // where Orchid will find them later
            *args.toTypedArray() // allow additional arbitrary args
        )

        val process = ProcessBuilder()
                .command(*processArgs)
                .redirectErrorStream(true)
                .start()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(callback(process.inputStream))
        val exitValue = process.waitFor()
        executor.shutdown()
        return exitValue == 0
    }

// Process Kss output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getKssRootdoc(destinationDir: Path): KssRootdoc {
        return KssRootdoc(
                emptyList()
        )
    }

}

package com.copperleaf.kodiak.swift

import com.caseyjbrooks.clog.Clog
import com.eden.common.util.IOStreamUtils
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * This class executes a sourcekitten command and returns the result as a String
 */
class SourcekittenWrapper(
    private val mainArgs: MainArgs,
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
) : AutoCloseable {

    /**
     * Cache the SourceKitten binary embedded in this jar into the local filesystem so it can be executed as a CLI tool
     */
    fun cacheSourceKittenBinary() {
        mainArgs.sourceKittenBinary.parent.toFile().mkdirs()
        Files.copy(
            this.javaClass.getResourceAsStream("/bin/sourcekitten"),
            mainArgs.sourceKittenBinary,
            StandardCopyOption.REPLACE_EXISTING
        )
        mainArgs.sourceKittenBinary.toFile().setExecutable(true)
    }

    /**
     * Print the version of the SourceKitten binary to the screen
     */
    fun printSourceKittenVersion() {
        Clog.i("SourceKitten version: {}", proc("version"))
    }

    /**
     * Execute a SourceKitten command in a child process, and return the stdout content as a String
     */
    fun proc(vararg processArgs: String) : String {
        val builder = ProcessBuilder()
            .directory(mainArgs.sourceKittenBinary.parent.toFile())
            .command("./sourcekitten", *processArgs)
            .redirectErrorStream(true)
        val process = builder.start()

        val collector = IOStreamUtils.InputStreamCollector(process.inputStream)

        val future = executor.submit(collector)

        // wait for SourceKitten process to complete
        process.waitFor()

        // then wait for thread to stop reading input into the buffer
        future.get(3, TimeUnit.SECONDS)

        val doc = collector.toString()

        return doc
    }

    override fun close() {
        executor.shutdown()
    }

}

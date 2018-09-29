package com.copperleaf.dokka.json

import kotlinx.io.InputStream
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class DokkaJsonRunnerTest {

    lateinit var client: OkHttpClient
    lateinit var cacheDir: Path
    lateinit var outputDir: Path
    lateinit var resolver: MavenResolver
    lateinit var dokkaRunner: KotlindocInvoker

    @BeforeEach
    internal fun setUp() {
        client = OkHttpClient.Builder().build()
        cacheDir = Files.createTempDirectory("DokkaJsonRunnerTest")
        outputDir = Files.createTempDirectory("dokkaTestOutput")
        outputDir.toFile().mkdirs()

        resolver = MavenResolverImpl(client, cacheDir)
        dokkaRunner = KotlindocInvokerImpl(resolver, outputDir, "com.github.copper-leaf.dokka-json:dokka-json:0.1.9")
    }

    @AfterEach
    internal fun tearDown() {
        cacheDir.toFile().deleteRecursively()
        outputDir.toFile().deleteRecursively()
    }

    @Test
    fun testRunningDokka() {
//        try {
//            val rootDoc = dokkaRunner.getRootDoc(
//                    listOf(
//                            File("../dokka-json/src/test/java").canonicalFile.toPath(),
//                            File("../dokka-json/src/test/kotlin").canonicalFile.toPath()
//                    ),
//                    emptyList()
//            ) { inputStream -> InputStreamPrinter(inputStream) }
//
//            for (f in outputDir.toFile().walk()) {
//                println("output file: ${f.absolutePath}")
//            }
//        }
//        catch(t: Throwable) {
//            println(t.message)
//            throw t
//        }

//        try {
//            expectThat(rootDoc)
//                    .and {
//                        chain { it.packages }.isNotEmpty()
//                    }
//                    .and {
//                        chain { it.classes }.isNotEmpty()
//                    }
//        }
//        catch (t: Throwable) {
//            println(t.message)
//            throw t
//        }
    }

}

class InputStreamPrinter(private val inputStream: InputStream) : Runnable {

    override fun run() {
        BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8"))).lines().forEach {
            println(it)
        }
    }
}
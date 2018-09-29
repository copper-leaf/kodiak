package com.copperleaf.dokka.json

import com.copperleaf.dokka.json.models.KotlinClassDoc
import com.copperleaf.dokka.json.models.KotlinPackageDoc
import com.copperleaf.dokka.json.models.KotlinRootdoc
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.InputStream
import java.io.StringReader
import java.nio.file.Path
import java.util.ArrayDeque
import java.util.concurrent.Executors
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

interface KotlindocInvoker {
    fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): KotlinRootdoc
}
interface MavenResolver {
    fun getMavenJars(vararg targets: String): List<Path>
}

class KotlindocInvokerImpl(
        private val mavenResolver:  MavenResolver,
        private val dokkaOutputPath: Path,
        private vararg val targets: String
) : KotlindocInvoker {

    override fun getRootDoc(sourceDirs: List<Path>, args: List<String>, callback: (InputStream) -> Runnable): KotlinRootdoc {
        val dokkaJarPaths = mavenResolver.getMavenJars(*targets)
        executeDokka(dokkaJarPaths, sourceDirs, args) { callback(it) }
        return getKotlinRootdoc()
    }

// Run Dokka
//----------------------------------------------------------------------------------------------------------------------

    private fun executeDokka(
            dokkaJarPath: List<Path>,
            sourceDirs: List<Path>,
            args: List<String>,
            callback: (InputStream) -> Runnable
    ) {
        val process = ProcessBuilder()
                .command(
                        "java",
                        "-classpath", dokkaJarPath.map { it.toFile().absolutePath }.joinToString(File.pathSeparator), // classpath of downloaded jars
                        "org.jetbrains.dokka.MainKt", // Dokka main class
                        "-format", "json", // JSON format (so we can pick up results afterwards)
                        "-noStdlibLink",
                        "-impliedPlatforms", "JVM",
                        "-src", sourceDirs.map { it.toFile().absolutePath }.joinToString(separator = File.pathSeparator), // the sources to process
                        "-output", dokkaOutputPath.toFile().absolutePath, // where Orchid will find them later
                        *args.toTypedArray() // allow additional arbitrary args
                )
                .start()

        Executors.newSingleThreadExecutor().submit(callback(process.inputStream))
        process.waitFor()
    }

// Process Dokka output to a model Orchid can use
//----------------------------------------------------------------------------------------------------------------------

    private fun getKotlinRootdoc(): KotlinRootdoc {
        val packages = getDokkaPackagePages()
        val classes = getDokkaClassPages()

        return KotlinRootdoc(
                packages,
                classes
        )
    }

    private fun getDokkaPackagePages(): List<KotlinPackageDoc> {
        val packagePagesList = ArrayList<KotlinPackageDoc>()
        dokkaOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name == "index.json" }
                .map { KotlinPackageDoc.fromJson(it.readText()) }
                .toCollection(packagePagesList)

        return packagePagesList
    }

    private fun getDokkaClassPages(): List<KotlinClassDoc> {
        val classPagesList = ArrayList<KotlinClassDoc>()
        dokkaOutputPath
                .toFile()
                .walkTopDown()
                .filter { it.isFile && it.name != "index.json" }
                .map { KotlinClassDoc.fromJson(it.readText()) }
                .toCollection(classPagesList)

        return classPagesList
    }

}

class MavenResolverImpl(
        private val client: OkHttpClient,
        private val cacheDir: Path
) : MavenResolver {

    val repos = listOf(
            "https://jcenter.bintray.com",
            "https://kotlin.bintray.com/kotlinx",
            "https://jitpack.io"
    )

    override fun getMavenJars(vararg targets: String): List<Path> {
        val processedTargets = HashSet<String>()
        val targetsToProcess = ArrayDeque<String>()
        targetsToProcess.addAll(targets)

        val resolvedJars = ArrayList<Path>()

        while (targetsToProcess.peek() != null) {
            val currentTarget = targetsToProcess.pop()

            // we've already processed this artifact, skip this iteration
            if (processedTargets.contains(currentTarget)) continue
            processedTargets.add(currentTarget)

            // otherwise resolve its dependencies and download this jar
            val groupId = currentTarget.split(":").getOrElse(0) { "" }
            val artifactId = currentTarget.split(":").getOrElse(1) { "" }
            val version = currentTarget.split(":").getOrElse(2) { "" }

            targetsToProcess.addAll(getTransitiveDependencies(groupId, artifactId, version))
            val downloadedJarPath = downloadJar(groupId, artifactId, version)
            if (downloadedJarPath != null) {
                resolvedJars.add(downloadedJarPath)
            }
        }

        return resolvedJars
    }

    private fun getTransitiveDependencies(groupId: String, artifactId: String, version: String): List<String> {
        for (repo in repos) {
            val pomUrl = "$repo/${groupId.replace(".", "/")}/$artifactId/$version/$artifactId-$version.pom"
            client.newCall(Request.Builder().url(pomUrl).build()).execute().use {
                if (it.isSuccessful) {
                    val mavenMetadataXml = it.body()?.string() ?: ""

                    val doc = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(InputSource(StringReader(mavenMetadataXml)))

                    val itemsTypeT1 = XPathFactory
                            .newInstance()
                            .newXPath()
                            .evaluate("/project/dependencies/dependency", doc, XPathConstants.NODESET) as NodeList

                    val transitiveDependencies = ArrayList<String>()

                    for (i in 0 until itemsTypeT1.length) {
                        var childGroupId = ""
                        var childArtifactId = ""
                        var childVersion = ""
                        var scope = ""
                        for (j in 0 until itemsTypeT1.item(i).childNodes.length) {
                            val name = itemsTypeT1.item(i).childNodes.item(j).nodeName
                            val value = itemsTypeT1.item(i).childNodes.item(j).textContent
                            when (name) {
                                "groupId"    -> childGroupId = value
                                "artifactId" -> childArtifactId = value
                                "version"    -> childVersion = value
                                "scope"      -> scope = value
                            }
                        }

                        if (scope == "compile") {
                            transitiveDependencies.add("$childGroupId:$childArtifactId:$childVersion")
                        }
                    }

                    return transitiveDependencies
                }
            }
        }

        return emptyList()
    }

    private fun downloadJar(groupId: String, artifactId: String, version: String): Path? {
        for (repo in repos) {
            val jarPath = "${groupId.replace('.', '/')}/$artifactId/$version"
            val jarName = "$artifactId-$version.jar"

            val downloadedFile = cacheDir.resolve("$jarPath/$jarName").toFile()

            if (downloadedFile.exists()) {
                return downloadedFile.toPath()
            }
            else {
                if(!downloadedFile.parentFile.exists()) {
                    downloadedFile.parentFile.mkdirs()
                }
                downloadedFile.createNewFile()

                val jarUrl = "$repo/$jarPath/$jarName"
                client.newCall(Request.Builder().url(jarUrl).build()).execute().use {
                    if (it.isSuccessful) {
                        val sink = Okio.buffer(Okio.sink(downloadedFile))
                        sink.writeAll(it.body()!!.source())
                        sink.close()

                        return downloadedFile.toPath()
                    }
                }
            }
        }

        return null
    }

}
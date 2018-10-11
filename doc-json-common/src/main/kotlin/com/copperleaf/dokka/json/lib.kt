package com.copperleaf.dokka.json

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.nio.file.Path
import java.util.ArrayDeque
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

interface MavenResolver {
    fun getMavenJars(targets: List<Artifact>): List<Artifact>

    fun getMavenJars(vararg targets: String): List<Artifact>
}
data class Artifact(
        val groupId: String,
        val artifactId: String,
        val version: String
) {

    var jarPath: Path? = null

    fun toTarget(): String {
        return "$groupId:$artifactId:$version"
    }

    fun getRemotePomUrl(repo: String): String {
        return "$repo/${groupId.replace(".", "/")}/$artifactId/$version/$artifactId-$version.pom"
    }

    fun getRemoteJarUrl(repo: String): String {
        return "$repo/${groupId.replace(".", "/")}/$artifactId/$version/$artifactId-$version.jar"
    }

    fun getCachePath(cacheDir: Path?): Path {
        val path = "${groupId.replace('.', '/')}/$artifactId/$version/$artifactId-$version.jar"
        return cacheDir?.resolve(path) ?: File(path).toPath()
    }

    companion object {
        @JvmStatic
        fun from(target: String): Artifact {
            // otherwise resolve its dependencies and download this jar
            val groupId = target.split(":").getOrElse(0) { "" }
            val artifactId = target.split(":").getOrElse(1) { "" }
            val version = target.split(":").getOrElse(2) { "" }
            return Artifact(groupId, artifactId, version)
        }

        @JvmStatic
        fun from(groupId: String, artifactId: String, version: String, jarPath: Path): Artifact {
            val artifact = Artifact(groupId, artifactId, version)
            artifact.jarPath = jarPath
            return artifact
        }
    }
}

class MavenResolverImpl(
        private val client: OkHttpClient,
        private val cacheDir: Path
) : MavenResolver {

    var repos = listOf(
            "https://jcenter.bintray.com",
            "https://kotlin.bintray.com/kotlinx",
            "https://jitpack.io"
    )

    override fun getMavenJars(vararg targets: String): List<Artifact> {
        return getMavenJars(targets.map { Artifact.from(it) })
    }

    override fun getMavenJars(targets: List<Artifact>): List<Artifact> {
        val processedTargets = HashSet<Artifact>()
        val targetsToProcess = ArrayDeque<Artifact>()
        targetsToProcess.addAll(targets)

        val resolvedJars = ArrayList<Artifact>()

        while (targetsToProcess.peek() != null) {
            val currentTarget = targetsToProcess.pop()

            // we've already processed this artifact, skip this iteration
            if (processedTargets.contains(currentTarget)) continue
            processedTargets.add(currentTarget)

            // add this jar's transitive dependencies (only for remote jars)
            targetsToProcess.addAll(getTransitiveDependencies(currentTarget))

            // try to fetch the jar (only for remote jars)
            if(currentTarget.jarPath == null) {
                currentTarget.jarPath = downloadJar(currentTarget)
            }

            // jar resolution successful, add it to the list
            if (currentTarget.jarPath != null) {
                resolvedJars.add(currentTarget)
            }
        }

        return resolvedJars
    }

    private fun getTransitiveDependencies(artifact: Artifact): List<Artifact> {
        // we have already fetched the jar, or are loading it locally. We don't have any more dependencies to load
        if(artifact.jarPath != null) return emptyList()

        // we have not fetched this jar, so we also need to fetch its dependencies
        for (repo in repos) {
            val pomUrl = artifact.getRemotePomUrl(repo)
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

                    val transitiveDependencies = ArrayList<Artifact>()

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
                            transitiveDependencies.add(Artifact(childGroupId, childArtifactId, childVersion))
                        }
                    }

                    return transitiveDependencies
                }
            }
        }

        return emptyList()
    }

    private fun downloadJar(artifact: Artifact): Path? {
        val downloadedFile = artifact.getCachePath(cacheDir).toFile()

        if (downloadedFile.exists()) {
            return downloadedFile.toPath()
        }
        else {
            for (repo in repos) {
                if(!downloadedFile.parentFile.exists()) {
                    downloadedFile.parentFile.mkdirs()
                }
                downloadedFile.createNewFile()

                val request = Request.Builder()
                        .url(artifact.getRemoteJarUrl(repo))
                        .build()

                client.newCall(request).execute().use {
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
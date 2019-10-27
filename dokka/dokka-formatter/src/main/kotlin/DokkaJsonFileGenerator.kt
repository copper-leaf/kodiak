package com.copperleaf.kodiak.kotlin

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.connectAllToParents
import com.copperleaf.kodiak.kotlin.formatter.qualifiedName
import com.copperleaf.kodiak.kotlin.formatter.toClassDoc
import com.copperleaf.kodiak.kotlin.formatter.toPackageDoc
import com.google.inject.Inject
import com.google.inject.name.Named
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FileLocation
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.appendExtension
import org.jetbrains.dokka.path
import java.io.File
import java.io.IOException
import java.util.ArrayDeque

class DokkaJsonFileGenerator @Inject constructor(@Named("outputDir") override val root: File) :
    NodeLocationAwareGenerator {

    override fun location(node: DocumentationNode): FileLocation {
        return FileLocation(fileForNode(node, "json"))
    }

    private fun fileForNode(node: DocumentationNode, subdirectory: String, extension: String = ""): File {
        return File(root.resolve(subdirectory), absolutePathToNode(node)).appendExtension(extension)
    }

    override fun buildPages(nodes: Iterable<DocumentationNode>) {
        if (nodes.none()) return

        val classes = ArrayList<DocumentationNode>()
        val packages = ArrayList<DocumentationNode>()

        val unvisitedNodes = ArrayDeque<DocumentationNode>()
        unvisitedNodes.addAll(nodes)

        while (unvisitedNodes.peek() != null) {
            val node = unvisitedNodes.pop()
            node.members.forEach { unvisitedNodes.add(it) }

            when {
                NodeKind.classLike.contains(node.kind) -> classes.add(node)
                node.kind == NodeKind.Package -> packages.add(node)
            }
        }

        renderClassNodes(classes)
        renderPackageNodes(packages)
    }

    private fun renderClassNodes(nodes: Iterable<DocumentationNode>) {
        nodes
            .forEach { itemInFile ->
                val file = fileForNode(itemInFile, "Class", "json")
                file.parentFile?.mkdirsOrFail()
                try {
                    file.writeText(itemInFile.toClassDoc(true).toJson())
                } catch (e: Throwable) {
                    println(e)
                }
            }
    }

    private fun renderPackageNodes(nodes: Iterable<DocumentationNode>) {
        connectAllToParents(
            // create initial packages, use common functionality to connect parent-child structures
            nodes.mapNotNull { packagedoc ->
                Clog.i("Loading packagedoc [${packagedoc.qualifiedName}]")
                try {
                    packagedoc.toPackageDoc(true)
                } catch (e: Exception) {
                    Clog.e("Failed to create json for packagedoc [${packagedoc.qualifiedName}]: ${e.message}")
                    null
                }
            },
            {
                it.item.copy(
                    parent = it.parentId ?: "",
                    subpackages = it.children.map { (it.item.node as DocumentationNode).toPackageDoc(false) }
                )
            },
            { it.id }
        ).forEach {
            // write package files to disk
            val file = fileForNode((it.node as DocumentationNode), "Package", "json")
            file.parentFile?.mkdirsOrFail()
            try {
                file.writeText(it.toJson())
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    override fun buildOutlines(nodes: Iterable<DocumentationNode>) {

    }

    override fun buildSupportFiles() {

    }

    override fun buildPackageList(nodes: Iterable<DocumentationNode>) {

    }

    private fun File.mkdirsOrFail() {
        if (!mkdirs() && !exists()) {
            throw IOException("Failed to create directory $this")
        }
    }

    private fun absolutePathToNode(qualifiedName: List<String>, isPackage: Boolean): String {
        val parts = qualifiedName.map { overrideIdentifierToFilename(it) }.filterNot { it.isEmpty() }
        return if (isPackage) {
            // leaf node, use file in owner's folder
            parts.joinToString("/") + (if (parts.none()) "" else "/") + "index"
        } else {
            parts.joinToString("/")
        }
    }

    private fun absolutePathToNode(node: DocumentationNode) =
        absolutePathToNode(node.path.map { it.name }, node.kind == NodeKind.Package)

    private fun overrideIdentifierToFilename(path: String): String {
        val escaped = path.replace('<', '-').replace('>', '-')
        val lowercase = escaped.replace("[A-Z]".toRegex()) { matchResult -> matchResult.value.toLowerCase() }
        return lowercase.replace('.', '/')
    }
}

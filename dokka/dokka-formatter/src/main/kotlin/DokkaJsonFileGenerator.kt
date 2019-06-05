package com.copperleaf.dokka.json.generator

import com.google.inject.Inject
import com.google.inject.name.Named
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FileLocation
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.NodeKind
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.appendExtension
import org.jetbrains.dokka.format
import org.jetbrains.dokka.path
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.ArrayDeque

class DokkaJsonFileGenerator @Inject constructor(@Named("outputDir") override val root: File) :
    NodeLocationAwareGenerator {

    @set:Inject(optional = true)
    lateinit var formatService: FormatService

    override fun location(node: DocumentationNode): FileLocation {
        return FileLocation(fileForNode(node, formatService.linkExtension))
    }

    private fun fileForNode(node: DocumentationNode, extension: String = ""): File {
        return File(root, absolutePathToNode(node)).appendExtension(extension)
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
                node.kind == NodeKind.Package          -> packages.add(node)
            }
        }

        renderNodes(classes)
        renderNodes(packages)
    }

    private fun renderNodes(nodes: Iterable<DocumentationNode>) {
        for ((file, items) in nodes.groupBy { fileForNode(it, formatService.extension) }) {

            file.parentFile?.mkdirsOrFail()
            try {
                FileOutputStream(file).use {
                    OutputStreamWriter(it, Charsets.UTF_8).use {
                        it.write(formatService.format(location(items.first()), items))
                    }
                }
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

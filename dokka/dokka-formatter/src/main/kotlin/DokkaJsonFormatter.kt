package com.copperleaf.kodiak.kotlin

import com.copperleaf.kodiak.kotlin.formatter.classLike
import com.copperleaf.kodiak.kotlin.formatter.packageLike
import com.copperleaf.kodiak.kotlin.formatter.toClassDoc
import com.copperleaf.kodiak.kotlin.formatter.toPackageDoc
import kotlinx.serialization.json.Json
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FormattedOutputBuilder

class DokkaJsonFormatter(val to: StringBuilder) : FormattedOutputBuilder {

    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val jsonModule = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        val node = nodes.first()

        if (node.classLike) {
            to.append(node.toClassDoc(true).toJson(jsonModule))
        } else if (node.packageLike) {
            to.append(node.toPackageDoc(true).toJson(jsonModule))
        } else {
            // ignore, we are only documenting classes and packages for now
        }
    }
}

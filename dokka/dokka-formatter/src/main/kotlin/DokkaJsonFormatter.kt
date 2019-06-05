package com.copperleaf.dokka.json.generator

import com.copperleaf.dokka.json.generator.formatter.classLike
import com.copperleaf.dokka.json.generator.formatter.packageLike
import com.copperleaf.dokka.json.generator.formatter.toClassDoc
import com.copperleaf.dokka.json.generator.formatter.toPackageDoc
import org.jetbrains.dokka.DocumentationNode
import org.jetbrains.dokka.FormattedOutputBuilder

class DokkaJsonFormatter(val to: StringBuilder) : FormattedOutputBuilder {

    override fun appendNodes(nodes: Iterable<DocumentationNode>) {
        val node = nodes.first()

        if (node.classLike) {
            to.append(node.toClassDoc(true).toJson())
        } else if (node.packageLike) {
            to.append(node.toPackageDoc().toJson())
        } else {
            // ignore, we are only documenting classes and packages for now
        }
    }

}

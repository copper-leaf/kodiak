package com.copperleaf.kodiak.common.modules

import com.copperleaf.kodiak.common.firstBy
import java.nio.file.Path

/**
 * Delegates to a list of other `ModuleLocator`s, returning the first one which has a valid result.
 */
internal class DelegatingModuleLocator(
    private val locators: List<ModuleLocator>
) : ModuleLocator {

    override fun getModuleRoots(rootPath: Path): List<Path> {
        return locators.firstBy({ it.getModuleRoots(rootPath) }, { it.isNotEmpty() })
    }

}
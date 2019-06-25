package com.copperleaf.kodiak.common.modules

import java.nio.file.Path

/**
 * A ModuleLocator
 */
interface ModuleLocator {

    fun getModuleRoots(rootPath: Path) : List<Path>

    companion object {
        fun from(vararg locators: ModuleLocator) : ModuleLocator {
            return DelegatingModuleLocator(locators.toList())
        }
    }
}
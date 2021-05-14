package com.copperleaf.kodiak.common.modules.impl.modulelocator

import com.copperleaf.kodiak.common.modules.ModuleLocator
import java.nio.file.Path

/**
 * A ModuleLocator
 */
class GradleModuleLocator : ModuleLocator {

    override fun getModuleRoots(rootPath: Path): List<Path> {
        return emptyList()
    }
}

package com.copperleaf.kodiak.common

import com.copperleaf.kodiak.common.modules.ModuleLocator

data class DocInvokerDescriptor(
    val defaultModuleLocator: ModuleLocator,
    val validFileExtensions: List<String>
)
package com.copperleaf.kodiak.kotlin

import com.google.inject.Binder
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.Formats.DefaultAnalysisComponent
import org.jetbrains.dokka.Formats.DefaultAnalysisComponentServices
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.Formats.KotlinAsKotlin
import org.jetbrains.dokka.Generator
import org.jetbrains.dokka.KotlinLanguageService
import org.jetbrains.dokka.LanguageService
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.OutlineFormatService
import org.jetbrains.dokka.PackageListService
import org.jetbrains.dokka.Utilities.bind
import org.jetbrains.dokka.Utilities.lazyBind
import org.jetbrains.dokka.Utilities.toOptional
import org.jetbrains.dokka.Utilities.toType

class DokkaJsonFormatDescriptor :
    FormatDescriptor,
    DefaultAnalysisComponent,
    DefaultAnalysisComponentServices by KotlinAsKotlin {

    val formatServiceClass = null
    val generatorServiceClass = DokkaJsonFileGenerator::class
    val outlineServiceClass = null
    val packageListServiceClass = null
    val languageServiceClass = KotlinLanguageService::class

    override fun configureOutput(binder: Binder): Unit = with(binder) {
        bind<Generator>() toType NodeLocationAwareGenerator::class
        bind<NodeLocationAwareGenerator>() toType generatorServiceClass
        bind(generatorServiceClass.java) // https://github.com/google/guice/issues/847

        bind<LanguageService>() toType languageServiceClass

        lazyBind<OutlineFormatService>() toOptional (outlineServiceClass)
        lazyBind<FormatService>() toOptional formatServiceClass
        lazyBind<PackageListService>() toOptional packageListServiceClass
    }
}

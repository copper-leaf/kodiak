package com.copperleaf.kodiak.kotlin

import com.google.inject.Binder
import org.jetbrains.dokka.FormatService
import org.jetbrains.dokka.Formats.DefaultAnalysisComponent
import org.jetbrains.dokka.Formats.FormatDescriptor
import org.jetbrains.dokka.Formats.KotlinAsKotlin
import org.jetbrains.dokka.FormattedOutputBuilder
import org.jetbrains.dokka.Generator
import org.jetbrains.dokka.KotlinLanguageService
import org.jetbrains.dokka.LanguageService
import org.jetbrains.dokka.Location
import org.jetbrains.dokka.NodeLocationAwareGenerator
import org.jetbrains.dokka.Utilities.bind
import org.jetbrains.dokka.Utilities.lazyBind
import org.jetbrains.dokka.Utilities.toOptional
import org.jetbrains.dokka.Utilities.toType

class DokkaJsonFormatDescriptor : FormatDescriptor, DefaultAnalysisComponent {

    override val elementSignatureProvider = KotlinAsKotlin.elementSignatureProvider
    override val javaDocumentationBuilderClass = KotlinAsKotlin.javaDocumentationBuilderClass
    override val packageDocumentationBuilderClass = KotlinAsKotlin.packageDocumentationBuilderClass
    override val sampleProcessingService = KotlinAsKotlin.sampleProcessingService

    override fun configureOutput(binder: Binder): Unit = with(binder) {
        bind<Generator>() toType DokkaJsonFileGenerator::class
        bind<NodeLocationAwareGenerator>() toType DokkaJsonFileGenerator::class

        bind<LanguageService>() toType KotlinLanguageService::class
        lazyBind<FormatService>() toOptional DokkaJsonFormatService::class
    }
}

class DokkaJsonFormatService : FormatService {
    override val extension: String get() = "json"

    override fun createOutputBuilder(to: StringBuilder, location: Location): FormattedOutputBuilder {
        return DokkaJsonFormatter(to)
    }
}
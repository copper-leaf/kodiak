package com.copperleaf.kodiak

/**
 * ```kotlin
 * class ExampleApplication : Application {
 *     override val version: String = "1.0.0"
 *     override val name: String = "Example Kotlin Application"
 *
 *     override fun start(vararg args: String) {
 *         for(i in 1..args.first().toInt()) {
 *             println("$i...")
 *             Thread.sleep(1000)
 *         }
 *     }
 * }
 * ```
 */
class KotlinClassWithCodeInComments

package com.copperleaf.kodiak.common

import kotlinx.serialization.Serializable

/**
 * A `CommentTag` represents the metadata for a particular documentation element at a specific property. It should be
 * _either_ a single String value _or_ a mapping of keys to values, but not both. The text description of each tag is a
 * list of `CommentComponent`, which should be joined together just like the main comment text.
 *
 * For example, `@param foo The Foo` describes the parameter of a function call, and has a map of values, one for each
 * parameter. In the `DocElement`, there will be a mapping from `param` to a CommentTag object with multiple values:
 *
 * ```kotlin
 * commentTags = mapOf(
 *     "param" to CommentTag(values = mapOf("foo" to "The Foo")
 * )
 * ```
 *
 * Alternatively, `@return The computation result` has only a single value, not a map of values.
 *
 * ```kotlin
 * commentTags = mapOf(
 *     "return" to CommentTag(value = "The computation result")
 * )
 * ```
 */
@Serializable
data class CommentTag(
    val value: List<CommentComponent>? = null,
    val values: Map<String, List<CommentComponent>>? = null
) {
    init {
        check((value != null) xor (values != null)) { "A CommentTag must have a single value or a map of values, but not both!" }
    }
}
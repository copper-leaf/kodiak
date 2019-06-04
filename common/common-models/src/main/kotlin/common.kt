package com.copperleaf.json.common

import kotlinx.serialization.Serializable

interface DocElement {
    /**
     * The kind
     */
    val kind: String

    /**
     * The unique identifier of this element. Other elements may refer to this specific element with just this ID
     */
    val id: String

    /**
     * The human-readable name of this element
     */
    val name: String

    /**
     * A list of comment components which, when concatenated together, creates the full inline comment description.
     */
    val commentComponents: List<CommentComponent>

    /**
     * A map of comment tags which represent the metadata for this element, either written in the comment text, or
     * inferred by the documentation tool
     */
    val commentTags: Map<String, CommentTag>
}

interface ElementType : DocElement {

    /**
     * The human-readble type of this element
     */
    val type: String

    /**
     * The unique identifier for the type representing this element
     */
    val typeId: String

    /**
     * A simple, rich signature for this type which, when concatenated together, creates the full declaration of this
     * type.
     */
    val signature: List<CommentComponent>
}


/**
 * A `CommentComponent` is a rich chunk of text parsed from a code comment.
 *
 * The full text for inline comments can be created by joining together a list of CommentComponents on their `text`.
 * Normal text should have a kind of `text`, and the `text` of the component is just the text as entered in the
 * comment. Sections of text that link to another code element have a kind of `link`, the `value` is an identifier
 * pointing to the element being referenced, and `text` is the text that should be displayed.
 */
@Serializable
data class CommentComponent(
    val kind: String,
    val text: String,
    val value: String? = null
) {
    companion object {
        const val TEXT = "text"
        const val LINK = "link"
    }
}

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
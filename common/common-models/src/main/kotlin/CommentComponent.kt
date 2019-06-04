package com.copperleaf.json.common

import kotlinx.serialization.Serializable

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

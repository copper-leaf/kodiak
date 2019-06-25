@file:UseExperimental(UnstableDefault::class)
package com.copperleaf.kodiak.common

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class CommentComponentTest {

    @Test
    fun testSerializerFor_CommentComponent() {
        val underTest = CommentComponent(
            kind = "testKind",
            text = "testText",
            value = "testValue"
        )
        val json = """
            |{
            |    "kind": "testKind",
            |    "text": "testText",
            |    "value": "testValue"
            |}
            """.trimMargin()

        val serialized = Json.indented.stringify(CommentComponent.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = Json.indented.parse(CommentComponent.serializer(), json)
        expectThat(parsed)
            .and { get { kind }.isEqualTo("testKind") }
            .and { get { text }.isEqualTo("testText") }
            .and { get { value }.isEqualTo("testValue") }

        expectThat(underTest).isEqualTo(parsed)
    }

    @Test
    fun testSerializerFor_CommentComponent_WithNullValue() {
        val underTest = CommentComponent(
            kind = "testKind",
            text = "testText",
            value = null
        )
        val json = """
            |{
            |    "kind": "testKind",
            |    "text": "testText",
            |    "value": null
            |}
            """.trimMargin()

        val serialized = Json.indented.stringify(CommentComponent.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = Json.indented.parse(CommentComponent.serializer(), json)
        expectThat(parsed)
            .and { get { kind }.isEqualTo("testKind") }
            .and { get { text }.isEqualTo("testText") }
            .and { get { value }.isNull() }

        expectThat(underTest).isEqualTo(parsed)
    }

}

@file:UseExperimental(UnstableDefault::class)
package com.copperleaf.kodiak.common

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class RichTextComponentTest {

    @Test
    fun testSerializerFor_RichTextComponent() {
        val underTest = RichTextComponent(
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

        val serialized = Json.indented.stringify(RichTextComponent.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = Json.indented.parse(RichTextComponent.serializer(), json)
        expectThat(parsed)
            .and { get { kind }.isEqualTo("testKind") }
            .and { get { text }.isEqualTo("testText") }
            .and { get { value }.isEqualTo("testValue") }

        expectThat(underTest).isEqualTo(parsed)
    }

    @Test
    fun testSerializerFor_RichTextComponent_WithNullValue() {
        val underTest = RichTextComponent(
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

        val serialized = Json.indented.stringify(RichTextComponent.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = Json.indented.parse(RichTextComponent.serializer(), json)
        expectThat(parsed)
            .and { get { kind }.isEqualTo("testKind") }
            .and { get { text }.isEqualTo("testText") }
            .and { get { value }.isNull() }

        expectThat(underTest).isEqualTo(parsed)
    }

}

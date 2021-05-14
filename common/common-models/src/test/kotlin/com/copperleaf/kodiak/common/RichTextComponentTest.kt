package com.copperleaf.kodiak.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RichTextComponentTest {

    @Test
    fun testSerializerFor_RichTextComponent() {
        val jsonModule = Json {
            prettyPrint = true
            encodeDefaults = true
        }

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

        val serialized = jsonModule.encodeToString(underTest)
        assertEquals(json, serialized)

        val parsed = jsonModule.decodeFromString(RichTextComponent.serializer(), json)
        assertEquals("testKind", parsed.kind)
        assertEquals("testText", parsed.text)
        assertEquals("testValue", parsed.value)

        assertEquals(underTest, parsed)
    }

    @Test
    fun testSerializerFor_RichTextComponent_WithNullValue() {
        val jsonModule = Json {
            prettyPrint = true
            encodeDefaults = true
        }

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

        val serialized = jsonModule.encodeToString(RichTextComponent.serializer(), underTest)
        assertEquals(json, serialized)

        val parsed = jsonModule.decodeFromString(RichTextComponent.serializer(), json)
        assertEquals("testKind", parsed.kind)
        assertEquals("testText", parsed.text)
        assertNull(parsed.value)

        assertEquals(underTest, parsed)
    }
}

package com.copperleaf.kodiak.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CommentTagTest {

    @Test
    fun testSerializeCommentTag_singleValue() {
        val jsonModule = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val underTest = CommentTag(
            value = listOf(
                RichTextComponent(
                    kind = "testKind",
                    text = "testText",
                    value = "testValue"
                )
            )
        )
        val json = """
            |{
            |    "value": [
            |        {
            |            "kind": "testKind",
            |            "text": "testText",
            |            "value": "testValue"
            |        }
            |    ],
            |    "values": null
            |}
            """.trimMargin()

        val serialized = jsonModule.encodeToString(CommentTag.serializer(), underTest)
        assertEquals(json, serialized)

        val parsed = jsonModule.decodeFromString(CommentTag.serializer(), json)
        assertNull(parsed.values)
        assertNotNull(parsed.value)
        assertEquals(1, parsed.value!!.size)

        val first = parsed.value!!.first()
        assertEquals("testKind", first.kind)
        assertEquals("testText", first.text)
        assertEquals("testValue", first.value)

        assertEquals(underTest, parsed)
    }

    @Test
    fun testSerializeCommentTag_valueMap() {
        val jsonModule = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val underTest = CommentTag(
            values = mapOf(
                "testKey" to listOf(
                    RichTextComponent(
                        kind = "testKind",
                        text = "testText",
                        value = "testValue"
                    )
                )
            )
        )
        val json = """
                |{
                |    "value": null,
                |    "values": {
                |        "testKey": [
                |            {
                |                "kind": "testKind",
                |                "text": "testText",
                |                "value": "testValue"
                |            }
                |        ]
                |    }
                |}
                """.trimMargin()

        val serialized = jsonModule.encodeToString(underTest)
        assertEquals(json, serialized)

        val parsed = jsonModule.decodeFromString(CommentTag.serializer(), json)
        assertNull(parsed.value)
        assertNotNull(parsed.values)
        assertEquals(1, parsed.values!!.size)

        val testKey = parsed.values!!["testKey"]
        assertNotNull(testKey)
        assertEquals(1, testKey.size)

        val first = testKey.first()
        assertEquals(first.kind, "testKind")
        assertEquals(first.text, "testText")
        assertEquals(first.value, "testValue")

        assertEquals(underTest, parsed)
    }

    @Test
    fun testSerializeCommentTag_bothValueAndValues_throws() {
        val comment = listOf(
            RichTextComponent(
                kind = "testKind",
                text = "testText",
                value = "testValue"
            )
        )

        assertFailsWith<IllegalStateException> {
            CommentTag(
                value = comment,
                values = mapOf(
                    "testKey" to comment
                )
            )
        }
    }

    @Test
    fun testSerializeCommentTag_neitherValueNorValues_throws() {
        assertFailsWith<IllegalStateException> {
            CommentTag(
                value = null,
                values = null
            )
        }
    }
}

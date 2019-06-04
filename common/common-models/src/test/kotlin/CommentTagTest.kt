package com.copperleaf.json.common

import kotlinx.serialization.json.JSON
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.first
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class CommentTagTest {

    @Test
    fun testSerializeCommentTag_singleValue() {
        val underTest = CommentTag(
            value = listOf(
                CommentComponent(
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

        val serialized = JSON.indented.stringify(CommentTag.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = JSON.indented.parse(CommentTag.serializer(), json)
        expectThat(parsed)
            .and { get { values }.isNull() }
            .and { get { value }
                .isNotNull()
                .hasSize(1)
                .first()
                .and { get { kind }.isEqualTo("testKind") }
                .and { get { text }.isEqualTo("testText") }
                .and { get { value }.isEqualTo("testValue") }
            }

        expectThat(underTest).isEqualTo(parsed)
    }

    @Test
    fun testSerializeCommentTag_valueMap() {
        val underTest = CommentTag(
            values = mapOf(
                "testKey" to listOf(
                    CommentComponent(
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
                |        "testKey":
                |        [
                |            {
                |                "kind": "testKind",
                |                "text": "testText",
                |                "value": "testValue"
                |            }
                |        ]
                |    }
                |}
                """.trimMargin()

        val serialized = JSON.indented.stringify(CommentTag.serializer(), underTest)
        expectThat(serialized).isEqualTo(json)

        val parsed = JSON.indented.parse(CommentTag.serializer(), json)
        expectThat(parsed)
            .and { get { value }.isNull() }
            .and { get { values }
                // has values
                .isNotNull()
                .hasSize(1)["testKey"]

                // values.testKey is not empty
                .isNotNull()
                .hasSize(1)
                .first()
                .and { get { kind }.isEqualTo("testKind") }
                .and { get { text }.isEqualTo("testText") }
                .and { get { value }.isEqualTo("testValue") }
            }

        expectThat(underTest).isEqualTo(parsed)
    }

    @Test
    fun testSerializeCommentTag_bothValueAndValues_throws() {
        val comment = listOf(
            CommentComponent(
                kind = "testKind",
                text = "testText",
                value = "testValue"
            )
        )

        expectThrows<IllegalStateException> {
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
        expectThrows<IllegalStateException> {
            CommentTag(
                value = null,
                values = null
            )
        }
    }

}

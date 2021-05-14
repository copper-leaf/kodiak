package com.copperleaf.kodiak.swift.internal.models

import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.swift.MainArgs
import com.copperleaf.kodiak.swift.formatter.toClassDoc
import com.copperleaf.kodiak.swift.formatter.toEnumCaseDoc
import com.copperleaf.kodiak.swift.formatter.toExtensionDoc
import com.copperleaf.kodiak.swift.formatter.toFunctionDoc
import com.copperleaf.kodiak.swift.formatter.toParameterDoc
import com.copperleaf.kodiak.swift.formatter.toTypeParameterDoc
import com.copperleaf.kodiak.swift.formatter.toTypealiasDoc
import com.copperleaf.kodiak.swift.formatter.toVariableDoc

enum class SwiftSubstructureKind(val kindName: String) {

    // top-level elements
    CLASS("class"),
    PROTOCOL("protocol"),
    ENUM("enum"),
    STRUCT("struct"),

    TYPEALIAS(""),
    GLOBAL_VARIABLE(""),

    EXTENSION(""),

    TOP_LEVEL_FUNCTION(""),

    // member elements
    ENUM_CASE(""),

    PARAMETER(""),
    GENERIC_TYPE_PARAMETER(""),

    STATIC_VARIABLE(""),
    CLASS_VARIABLE(""),
    INSTANCE_VARIABLE(""),
    LOCAL_VARIABLE(""),

    INIT_METHOD(""),
    STATIC_METHOD(""),
    CLASS_METHOD(""),
    INSTANCE_METHOD(""),

    SUBSCRIPT_FUNCTION(""),

    IGNORED("")
    ;

    companion object {
        fun parse(input: String, name: String): SwiftSubstructureKind {
            return when {
                input == "source.lang.swift.decl.class" -> CLASS
                input == "source.lang.swift.decl.protocol" -> PROTOCOL
                input == "source.lang.swift.decl.enum" -> ENUM
                input == "source.lang.swift.decl.struct" -> STRUCT

                input == "source.lang.swift.decl.typealias" -> TYPEALIAS
                input == "source.lang.swift.decl.var.global" -> GLOBAL_VARIABLE

                input == "source.lang.swift.decl.extension" -> EXTENSION

                input == "source.lang.swift.decl.function.free" -> TOP_LEVEL_FUNCTION

                input == "source.lang.swift.decl.enumcase" -> ENUM_CASE

                input == "source.lang.swift.decl.var.static" -> STATIC_VARIABLE
                input == "source.lang.swift.decl.var.class" -> CLASS_VARIABLE
                input == "source.lang.swift.decl.var.instance" -> INSTANCE_VARIABLE
                input == "source.lang.swift.decl.var.local" -> LOCAL_VARIABLE

                input == "source.lang.swift.decl.var.parameter" -> PARAMETER
                input == "source.lang.swift.decl.generic_type_param" -> GENERIC_TYPE_PARAMETER

                input == "source.lang.swift.decl.function.method.static" -> STATIC_METHOD
                input == "source.lang.swift.decl.function.method.class" -> CLASS_METHOD
                input == "source.lang.swift.decl.function.method.instance" && name.startsWith("init") -> {
                    INIT_METHOD
                }
                input == "source.lang.swift.decl.function.method.instance" && !name.startsWith("init") -> {
                    INSTANCE_METHOD
                }
                input == "source.lang.swift.decl.function.subscript" -> SUBSCRIPT_FUNCTION

//                input in listOf(
//                    "source.lang.swift.expr.call"
//                )                                                                                      -> IGNORED
//
//                else                                                                                   -> throw Exception(
//                    "Unexpected substructure kind [$input]"
//                )

                else -> IGNORED
            }
        }
    }

    fun format(
        mainArgs: MainArgs,
        substructure: SourceKittenSubstructure,
        structure: SourceKittenSubstructure
    ): DocElement? {
        return when (this) {
            CLASS -> substructure.toClassDoc(mainArgs, structure, true)
            PROTOCOL -> substructure.toClassDoc(mainArgs, structure, true)
            ENUM -> substructure.toClassDoc(mainArgs, structure, true)
            STRUCT -> substructure.toClassDoc(mainArgs, structure, true)

            TYPEALIAS -> substructure.toTypealiasDoc(structure)
            GLOBAL_VARIABLE -> substructure.toVariableDoc(structure)

            EXTENSION -> substructure.toExtensionDoc(mainArgs, structure)

            TOP_LEVEL_FUNCTION -> substructure.toFunctionDoc(structure)

            ENUM_CASE -> substructure.toEnumCaseDoc(mainArgs, structure)

            PARAMETER -> substructure.toParameterDoc()
            GENERIC_TYPE_PARAMETER -> substructure.toTypeParameterDoc()

            STATIC_VARIABLE -> substructure.toVariableDoc(structure)
            CLASS_VARIABLE -> substructure.toVariableDoc(structure)
            INSTANCE_VARIABLE -> substructure.toVariableDoc(structure)
            LOCAL_VARIABLE -> substructure.toVariableDoc(structure)

            INIT_METHOD -> substructure.toFunctionDoc(structure)
            STATIC_METHOD -> substructure.toFunctionDoc(structure)
            CLASS_METHOD -> substructure.toFunctionDoc(structure)
            INSTANCE_METHOD -> substructure.toFunctionDoc(structure)
            SUBSCRIPT_FUNCTION -> substructure.toFunctionDoc(structure)

            IGNORED -> null
        }
    }
}

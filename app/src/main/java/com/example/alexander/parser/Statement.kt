package com.example.alexander.parser

data class Statement(val type: Type, val start: Int, var end: Int = -1) {

    companion object {
        enum class Type(val open: String, val close: String) {
            ROUND("(", ")"), SQUARE("[", "]"), BRACE("{", "}"), UNKNOWN("", "");

            companion object {

                fun get(element: String): Type {
                    Type.values().forEach {
                        if (it.open == element || it.close == element) return it
                    }
                    return UNKNOWN
                }

                fun open(element: String): Boolean? {
                    Type.values().forEach {
                        if (it.open == element) return true
                        if (it.close == element) return false
                    }
                    return null
                }
            }

        }
    }
}
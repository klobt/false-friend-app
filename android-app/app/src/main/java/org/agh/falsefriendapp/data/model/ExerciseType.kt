package org.agh.falsefriendapp.data.model

enum class ExerciseType(
    val apiValue: String
) {
    TRANSLATION("translation"),
    DEFINITION("definition"),
    MATCH("connect");

    companion object {
        fun fromValue(value: String): ExerciseType? {
            return entries.find { it.apiValue == value }
        }
    }
}

package org.agh.falsefriendapp.navigation

import org.agh.falsefriendapp.data.model.ExerciseType

object Routes {
    const val USER_HOME = "userHome"
    const val TRANSLATION = "translation"
    const val DEFINITION = "definition"
    const val MATCH = "match"
    const val SETTINGS = "settings"
    const val SUMMARY = "summary/{score}/{totalQuestions}/{exerciseType}"

    fun summary(score: Int, totalQuestions: Int, type: ExerciseType): String {
        return "summary/$score/$totalQuestions/${type.apiValue}"
    }
}

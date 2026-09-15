package org.agh.falsefriendapp.ui.state

sealed class ReviewItem {
    data class Choice(
        val question: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        val selectedAnswerIndex: Int
    ) : ReviewItem() {
        val correct: Boolean get() = selectedAnswerIndex == correctAnswerIndex
    }

    data class Match(
        val pairs: List<MatchPairResult>
    ) : ReviewItem() {
        val correctCount: Int get() = pairs.count { it.correct }
    }
}

data class MatchPairResult(
    val leftText: String,
    val selectedText: String,
    val correctText: String,
    val correct: Boolean
)

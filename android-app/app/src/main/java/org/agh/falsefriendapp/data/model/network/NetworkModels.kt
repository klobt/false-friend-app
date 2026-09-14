package org.agh.falsefriendapp.data.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseExerciseResponse(
    val data: List<BaseExerciseDto>
)

@Serializable
data class BaseExerciseDto(
    val id: Int,
    val type: Int,
    val data: BaseExerciseContentDto
)

@Serializable
data class BaseExerciseContentDto(
    val word: String,
    val answers: List<String>,
    @SerialName("correct_idx")
    val correctIdx: Int
)

@Serializable
data class MatchExerciseResponse(
    val data: List<MatchExerciseDto>
)

@Serializable
data class MatchExerciseDto(
    val id: Int,
    val type: Int,
    val data: MatchExerciseContentDto
)

@Serializable
data class MatchExerciseContentDto(
    val left: List<String>,
    val right: List<String>
)

@Serializable
data class ExercisesIds(
    @SerialName("exercise_ids")
    val exercisesIds: List<Int>
)

@Serializable
data class SessionRequest(
    @SerialName("user_id")
    val userId: Int,
    val results: List<SessionResultRequest>
)

@Serializable
data class SessionResultRequest(
    @SerialName("exercise_id")
    val exerciseId: Int,
    val correct: Boolean,
    @SerialName("time_ms")
    val timeMs: Long
)

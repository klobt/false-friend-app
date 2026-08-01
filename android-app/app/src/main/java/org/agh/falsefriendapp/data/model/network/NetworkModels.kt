package org.agh.falsefriendapp.data.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseResponse(
    val data: List<ExerciseDto>
)

@Serializable
data class ExerciseDto(
    val id: Int,
    val type: Int,
    @SerialName("data")
    val content: ExerciseContentDto
)

@Serializable
data class ExerciseContentDto(
    val word: String,
    val answers: List<String>,
    @SerialName("correct_idx")
    val correctIdx: Int
)

@Serializable
data class ExercisesIds(
    @SerialName("exercise_ids")
    val exercisesIds: List<Int>
)

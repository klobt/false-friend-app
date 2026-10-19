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

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)

@Serializable
data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String
)

@Serializable
data class UserIdsResponse(
    val data: List<Int>
)

@Serializable
data class FriendRequestResponse(
    val status: String
)

@Serializable
data class FriendActionRequest(
    val accept: Boolean
)

@Serializable
data class UserDto(
    val id: Int,
    val nickname: String? = null,
    @SerialName("public_data")
    val publicData: PublicDataDto = PublicDataDto()
)

@Serializable
data class PublicDataDto(
    val avatar: String? = null
)

@Serializable
data class PublicDataRequest(
    val data: PublicDataDto
)

@Serializable
data class UsersResponse(
    val data: List<UserDto>
)

@Serializable
data class NicknameRequest(
    val nickname: String
)

@Serializable
data class UserStatsDto(
    @SerialName("current_streak")
    val currentStreak: Int,
    @SerialName("total_correct")
    val totalCorrect: Int,
    @SerialName("total_answers")
    val totalAnswers: Int
)

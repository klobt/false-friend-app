package org.agh.falsefriendapp.data.model.network

import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.model.Session
import org.agh.falsefriendapp.data.model.UserProfile

fun BaseExerciseDto.toBaseExercise(): BaseExercise {
    return BaseExercise(
        id = id,
        sentence = data.word,
        correctAnswerIndex = data.correctIdx,
        options = data.answers
    )
}

fun MatchExerciseDto.toMatchExercise(): MatchExercise {
    return MatchExercise(
        id = id,
        left = data.left,
        right = data.right
    )
}

fun Session.toRequest(): SessionRequest {
    return SessionRequest(
        results = results.map { result ->
            SessionResultRequest(
                exerciseId = result.exerciseId,
                correct = result.correct,
                timeMs = result.timeMs
            )
        }
    )
}

fun UserDto.toUserProfile(): UserProfile {
    return UserProfile(
        id = id,
        nickname = nickname,
        avatar = Avatar.fromKey(publicData.avatar)
    )
}

fun UserDto.toFriend(): Friend {
    return Friend(
        id = id,
        nickname = nickname,
        avatar = Avatar.fromKey(publicData.avatar)
    )
}

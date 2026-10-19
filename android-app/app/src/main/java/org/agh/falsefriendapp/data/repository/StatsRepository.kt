package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.UserStats
import org.agh.falsefriendapp.data.model.network.toUserStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    private val userApi: UserApi
) {
    private val _stats = MutableStateFlow<UserStats?>(null)
    val stats = _stats.asStateFlow()

    suspend fun refreshStats() {
        _stats.value = try {
            userApi.getStats().toUserStats()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }
}

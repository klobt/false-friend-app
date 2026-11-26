package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.data.repository.StatsRepository
import org.agh.falsefriendapp.navigation.Routes
import org.agh.falsefriendapp.ui.state.StatsUiState
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "StatsViewModel"
private const val FORBIDDEN = 403

@HiltViewModel
class StatsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val statsRepository: StatsRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {
    private val friendId = savedStateHandle.get<String>(Routes.USER_ID_ARG)?.toIntOrNull()

    private val _state = MutableStateFlow(StatsUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val current = StatsUiState(today = LocalDate.now())

            try {
                val loaded = coroutineScope {
                    val days = async {
                        statsRepository.getDailyAccuracy(
                            current.firstDay(),
                            current.today,
                            friendId
                        )
                    }
                    val friend = friendId?.let { id ->
                        async { friendRepository.getFriend(id) }
                    }
                    current.copy(days = days.await(), friend = friend?.await())
                }
                _state.update { loaded }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load stats", e)
                _state.update {
                    current.copy(
                        loadFailed = true,
                        notFriends = e is HttpException && e.code() == FORBIDDEN
                    )
                }
            }
        }
    }
}

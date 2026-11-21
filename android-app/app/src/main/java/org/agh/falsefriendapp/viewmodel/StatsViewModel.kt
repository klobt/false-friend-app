package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.StatsRepository
import org.agh.falsefriendapp.ui.state.StatsUiState
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "StatsViewModel"

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatsUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val current = StatsUiState(today = LocalDate.now())

            try {
                val days = statsRepository.getDailyAccuracy(
                    current.firstDay(),
                    current.today
                )
                _state.update {
                    current.copy(days = days)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load stats", e)
                _state.update {
                    current.copy(loadFailed = true)
                }
            }
        }
    }
}

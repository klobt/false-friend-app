package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.auth.SessionEvents
import org.agh.falsefriendapp.data.repository.LoginRepository
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: LoginRepository,
    sessionEvents: SessionEvents
) : ViewModel() {
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()
    val forcedLogout = sessionEvents.forcedLogout

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedOut.value = true
        }
    }
}

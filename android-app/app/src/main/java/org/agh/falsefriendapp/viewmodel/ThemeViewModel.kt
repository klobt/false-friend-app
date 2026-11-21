package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.agh.falsefriendapp.data.model.ThemeMode
import org.agh.falsefriendapp.data.settings.SettingsStore
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    settingsStore: SettingsStore
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode?> = settingsStore.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

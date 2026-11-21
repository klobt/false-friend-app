package org.agh.falsefriendapp.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.agh.falsefriendapp.data.model.ThemeMode
import java.io.IOException

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

class SettingsStore(private val dataStore: DataStore<Preferences>) {
    val themeMode: Flow<ThemeMode> = dataStore.data.catch { e ->
        if (e is IOException) {
            emit(emptyPreferences())
        }
        else {
            throw e
        }
    }.map { preferences ->
        ThemeMode.fromKey(preferences[THEME_MODE_KEY])
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.key
        }
    }
}

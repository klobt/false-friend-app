package org.agh.falsefriendapp.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val KEY = stringPreferencesKey("access_token")

class TokenStore(private val dataStore: DataStore<Preferences>) {
    val token: Flow<String?> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw e
            }
        }
        .map { preferences ->
            preferences[KEY]
        }

    suspend fun read(): String? {
        return token.first()
    }

    suspend fun save(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY] = token
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(KEY)
        }
    }
}

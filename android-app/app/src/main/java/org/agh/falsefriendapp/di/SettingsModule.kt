package org.agh.falsefriendapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.agh.falsefriendapp.data.settings.SettingsStore
import org.agh.falsefriendapp.data.settings.settingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsStore(@ApplicationContext context: Context): SettingsStore {
        return SettingsStore(context.settingsDataStore)
    }
}

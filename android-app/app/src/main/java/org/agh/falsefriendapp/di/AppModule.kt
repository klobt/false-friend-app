package org.agh.falsefriendapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.agh.falsefriendapp.data.auth.TokenStore
import org.agh.falsefriendapp.data.auth.authDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore {
        return TokenStore(context.authDataStore)
    }
}

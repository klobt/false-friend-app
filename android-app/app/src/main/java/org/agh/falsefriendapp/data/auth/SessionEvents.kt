package org.agh.falsefriendapp.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionEvents @Inject constructor() {
    private val _forcedLogout = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val forcedLogout: SharedFlow<Unit> = _forcedLogout.asSharedFlow()

    fun emitForcedLogout() {
        _forcedLogout.tryEmit(Unit)
    }
}

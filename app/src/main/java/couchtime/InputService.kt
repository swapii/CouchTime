package couchtime

import android.media.tv.TvInputService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class InputService : TvInputService() {

    @Inject
    @Suppress("ProtectedInFinal")
    protected lateinit var playerSession: Provider<PlayerSession>

    init {
        Timber.d("init")
    }

    override fun onCreateSession(inputId: String): Session {
        Timber.d("Create session for inputId [$inputId]")
        return playerSession.get()
    }

}

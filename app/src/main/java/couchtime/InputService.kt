package couchtime

import android.media.tv.TvInputService
import couchtime.PlayerSession
import timber.log.Timber

class InputService : TvInputService() {

    init {
        Timber.d("init")
    }

    override fun onCreateSession(inputId: String): Session {
        Timber.d("Create session for inputId [$inputId]")
        return PlayerSession(context = this)
    }

}

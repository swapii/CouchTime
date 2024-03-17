package couchtime

import android.media.tv.TvInputService
import timber.log.Timber

class InputService : TvInputService() {

    init {
        Timber.d("init")
    }

    override fun onCreateSession(inputId: String): Session? {
        Timber.d("onCreateSession, inputId=[$inputId]")
        return null
    }

}

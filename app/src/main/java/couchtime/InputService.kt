package couchtime

import android.media.tv.TvInputService

class InputService : TvInputService() {

    override fun onCreateSession(inputId: String): Session? {
        return null
    }

}

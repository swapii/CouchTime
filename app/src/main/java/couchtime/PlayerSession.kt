package couchtime

import android.content.ComponentName
import android.content.Context
import android.media.tv.TvContract
import android.media.tv.TvInputService.Session
import android.net.Uri
import android.view.Surface
import timber.log.Timber

class PlayerSession(
    context: Context,
) : Session(context) {

    override fun onSetSurface(surface: Surface?): Boolean {
        Timber.d("Set surface")
        return true
    }

    override fun onTune(channelUri: Uri?): Boolean {
        Timber.d("Tune to channel [$channelUri]")
        return true
    }

    override fun onSetStreamVolume(volume: Float) {
        Timber.d("Set stream volume [$volume]")
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Timber.d("Set caption enabled [$enabled]")
    }

    override fun onRelease() {
        Timber.d("Release")
    }

    companion object {

        fun createInputId(context: Context): String =
            TvContract.buildInputId(
                ComponentName(
                    /* pkg = */ context,
                    /* cls = */ PlayerSession::class.java,
                ),
            )

    }

}

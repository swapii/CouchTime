package couchtime

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.media.tv.TvInputService.Session
import android.net.Uri
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import couchtime.core.m3u.PlaylistChannelData
import couchtime.feature.sync.GetPlaylistChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

class PlayerSession @Inject constructor(
    private val context: Context,
    private val getPlaylistChannels: GetPlaylistChannels,
) : Session(context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val player = ExoPlayer.Builder(context).build()

    private val channels: StateFlow<List<PlaylistChannelData>> =
        flow { emit(getPlaylistChannels().toList()) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    override fun onSetSurface(surface: Surface?): Boolean {
        Timber.d("Set surface")
        player.setVideoSurface(surface)
        return true
    }

    override fun onTune(channelUri: Uri): Boolean {
        Timber.d("Tune to channel [$channelUri]")
        try {
            val channelDomainId: Long =
                context.contentResolver
                    .query(
                        /* uri = */ channelUri,
                        /* projection = */ arrayOf(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID),
                        /* queryArgs = */ null,
                        /* cancellationSignal = */ null,
                    )!!
                    .use { cursor: Cursor ->
                        check(cursor.count == 1)
                        val columnIndex: Int = cursor.getColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID)
                        check(columnIndex >= 0)
                        cursor.moveToFirst()
                        cursor.getString(columnIndex).toLong()
                    }

            val channel: PlaylistChannelData =
                channels.value
                    .first { it.id == channelDomainId }

            val channelAddress = channel.address
            Timber.d("Channel address [$channelAddress]")

            player.setMediaItem(MediaItem.fromUri(channelAddress))
            player.prepare()
            player.play()
            notifyVideoAvailable()

            return true
        } catch (e: Exception) {
            val message = "Can't tune to channel [$channelUri]"
            val exception = Exception(message, e)
            Timber.e(exception, message)
            return false
        }
    }

    override fun onSetStreamVolume(volume: Float) {
        Timber.d("Set stream volume [$volume]")
        player.volume = volume
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Timber.d("Set caption enabled [$enabled]")
    }

    override fun onRelease() {
        Timber.d("Release")
        player.release()
        coroutineScope.cancel()
    }

}

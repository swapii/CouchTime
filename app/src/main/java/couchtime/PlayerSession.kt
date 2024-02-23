package couchtime

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.media.tv.TvInputManager
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.impl.extensions.get
import timber.log.Timber
import javax.inject.Inject

class PlayerSession @Inject constructor(
    private val context: Context,
    private val getPlaylistChannels: GetPlaylistChannels,
) : Session(context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val channelsList: Store<Unit, List<PlaylistChannelData>> =
        StoreBuilder
            .from(
                fetcher = Fetcher.of { _: Unit ->
                    getPlaylistChannels()
                        .toList()
                },
            )
            .cachePolicy(
                MemoryPolicy.builder<Unit, List<PlaylistChannelData>>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val channels: Store<Long, PlaylistChannelData> =
        StoreBuilder
            .from(
                fetcher = Fetcher.of { channelId: Long ->
                    channelsList.get(Unit).first { it.id == channelId }
                },
            )
            .cachePolicy(
                MemoryPolicy.builder<Long, PlaylistChannelData>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val channelIds: Store<Uri, Long> =
        StoreBuilder.from(
            Fetcher.of { channelUri: Uri ->
                withContext(Dispatchers.IO) {
                    context.contentResolver
                        .query(
                            /* uri = */ channelUri,
                            /* projection = */ arrayOf(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID),
                            /* queryArgs = */ null,
                            /* cancellationSignal = */ null,
                        )!!
                        .use { cursor: Cursor ->
                            check(cursor.count == 1)
                            val columnIndex: Int =
                                cursor.getColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID)
                            check(columnIndex >= 0)
                            cursor.moveToFirst()
                            cursor.getString(columnIndex).toLong()
                        }
                }
            }
        )
            .cachePolicy(
                MemoryPolicy.builder<Uri, Long>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val mediaItemsStore: Store<Uri, MediaItem> =
        StoreBuilder
            .from(
                fetcher = Fetcher.of { channelUri: Uri ->
                    val channelDomainId: Long = channelIds.get(channelUri)
                    channels.get(channelDomainId)
                        .let {
                            MediaItem.fromUri(it.address)
                        }
                }
            )
            .cachePolicy(
                MemoryPolicy.builder<Uri, MediaItem>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val channelUri = MutableStateFlow<Uri?>(null)

    private val player = ExoPlayer.Builder(context).build()

    init {
        coroutineScope.launch {
            channelUri
                .collectLatest { channelUri ->
                    withContext(Dispatchers.Main.immediate) {
                        if (channelUri == null) {
                            player.stop()
                        } else {
                            val mediaItem = mediaItemsStore.get(channelUri)
                            try {
                                player.setMediaItem(mediaItem)
                                player.prepare()
                                player.play()
                                notifyVideoAvailable()
                            } catch (e: Exception) {
                                val message = "Can't tune to channel [$channelUri]"
                                val exception = Exception(message, e)
                                Timber.e(exception, message)
                                notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)
                            }
                        }
                    }
                }
        }
    }

    override fun onSetSurface(surface: Surface?): Boolean {
        Timber.d("Set surface")
        player.setVideoSurface(surface)
        return true
    }

    override fun onTune(channelUri: Uri): Boolean {
        Timber.d("Tune to channel [$channelUri]")
        this.channelUri.value = channelUri
        return true
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

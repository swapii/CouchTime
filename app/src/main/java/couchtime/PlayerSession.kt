package couchtime

import android.content.Context
import android.media.tv.TvInputManager
import android.media.tv.TvInputService.Session
import android.net.Uri
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.tvcontract.domain.model.TvContractDisplayNumber
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelId
import couchtime.feature.channel.domain.model.asChannelId
import couchtime.feature.channel.domain.source.LocalChannelsSource
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
    context: Context,
    private val localChannelsSource: LocalChannelsSource,
    private val tvContractChannelsSource: TvContractChannelsSource,
) : Session(context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val channels: Store<ChannelId, Channel> =
        StoreBuilder
            .from(
                fetcher = Fetcher.of { id: ChannelId ->
                    localChannelsSource.getChannel(id)
                },
            )
            .cachePolicy(
                MemoryPolicy.builder<ChannelId, Channel>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val channelIds: Store<TvContractChannelAddress, ChannelId> =
        StoreBuilder.from(
            Fetcher.of { channelAddress: TvContractChannelAddress ->
                tvContractChannelsSource.getChannelInternalProviderId(channelAddress).asChannelId()
            }
        )
            .cachePolicy(
                MemoryPolicy.builder<TvContractChannelAddress, ChannelId>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val mediaItemsStore: Store<TvContractChannelAddress, MediaItem> =
        StoreBuilder
            .from(
                fetcher = Fetcher.of { channelAddress: TvContractChannelAddress ->
                    val channelId: ChannelId = channelIds.get(channelAddress)
                    channels.get(channelId)
                        .let {
                            MediaItem.fromUri(it.address)
                        }
                }
            )
            .cachePolicy(
                MemoryPolicy.builder<TvContractChannelAddress, MediaItem>()
                    .build()
            )
            .scope(coroutineScope)
            .build()

    private val channel = MutableStateFlow<TvContractChannelAddress?>(null)

    private val player = ExoPlayer.Builder(context).build()

    init {
        coroutineScope.launch {
            channel
                .collectLatest { channel: TvContractChannelAddress? ->
                    withContext(Dispatchers.Main.immediate) {
                        player.stop()
                        player.clearMediaItems()
                        notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING)
                        if (channel != null) {
                            val mediaItem = mediaItemsStore.get(channel)
                            try {
                                player.setMediaItem(mediaItem)
                                player.prepare()
                                player.play()
                                notifyVideoAvailable()
                            } catch (e: Exception) {
                                val message = "Can't tune to channel [$channel]"
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
        this.channel.value = TvContractChannelAddress(channelUri)
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

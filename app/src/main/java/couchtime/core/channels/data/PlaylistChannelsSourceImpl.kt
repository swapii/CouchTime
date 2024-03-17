package couchtime.core.channels.data

import android.content.Context
import androidx.core.net.toUri
import couchtime.core.channels.model.ChannelId
import couchtime.core.channels.model.PlaylistChannel
import couchtime.core.channels.source.PlaylistChannelsSource
import couchtime.core.m3u.M3uPlaylistItem
import couchtime.core.m3u.parseM3uPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class PlaylistChannelsSourceImpl @Inject constructor(
    private val context: Context,
) : PlaylistChannelsSource {

    override suspend fun readAll(): Flow<PlaylistChannel> {
        Timber.d("Get playlist channels")
        val applicationId = context.packageName
        return withContext(Dispatchers.IO) {
            sequence {
                File("/data/local/tmp/$applicationId.playlist")
                    .bufferedReader()
                    .use { reader ->
                        yieldAll(reader.lineSequence())
                    }
            }
                .parseM3uPlaylist()
                .map(M3uPlaylistItem::toPlaylistChannel)
                .asFlow()
        }
    }

}

private fun M3uPlaylistItem.toPlaylistChannel(): PlaylistChannel {

    val addressString = address
    val addressSuffix = "/index.m3u8"
    require(addressString.endsWith(addressSuffix))

    val id: Long = addressString.removeSuffix(addressSuffix).takeLastWhile { it.isDigit() }.toLong()

    val address = addressString.toUri()

    return PlaylistChannel(
        id = ChannelId(id),
        name = extInf.displayTitle,
        group = extGrp,
        address = address,
    )
}

package couchtime.feature.sync

import android.content.Context
import couchtime.core.m3u.PlaylistChannelData
import couchtime.core.m3u.parsePlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class GetPlaylistChannels(
    private val context: Context,
) {

    suspend operator fun invoke(): Sequence<PlaylistChannelData> {
        Timber.d("Get playlist channels")
        val applicationId = context.packageName
        return withContext(Dispatchers.IO) {
            sequence {
                File("/data/local/tmp/$applicationId.playlist")
                    .bufferedReader()
                    .use { reader ->
                        yieldAll(reader.lineSequence())
                    }
            }.parsePlaylist()
        }
    }

}

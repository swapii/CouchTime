package couchtime.feature.sync

import android.content.Context
import couchtime.core.m3u.M3uPlaylistItem
import couchtime.core.m3u.parseM3uPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class GetPlaylistChannels @Inject constructor(
    private val context: Context,
) {

    suspend operator fun invoke(): Sequence<M3uPlaylistItem> {
        Timber.d("Get playlist channels")
        val applicationId = context.packageName
        return withContext(Dispatchers.IO) {
            sequence {
                File("/data/local/tmp/$applicationId.playlist")
                    .bufferedReader()
                    .use { reader ->
                        yieldAll(reader.lineSequence())
                    }
            }.parseM3uPlaylist()
        }
    }

}

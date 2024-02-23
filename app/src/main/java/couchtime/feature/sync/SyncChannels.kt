package couchtime.feature.sync

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.tv.TvContract
import android.net.Uri
import couchtime.core.m3u.PlaylistChannelData
import timber.log.Timber

internal class SyncChannels(
    private val context: Context,
    private val getPlaylistChannels: GetPlaylistChannels,
) {

    suspend operator fun invoke(inputId: String) {
        Timber.d("Sync channels")

        val contentResolver = context.contentResolver

        val count: Int =
            contentResolver.count(TvContract.Channels.CONTENT_URI)

        if (count > 0) {
            Timber.d("Deleting $count existing rows")
            contentResolver.delete(TvContract.Channels.CONTENT_URI, null, null)
        }

        var newElementsCount = 0

        getPlaylistChannels()
            .map { channelData ->
                ContentProviderOperation.newInsert(TvContract.Channels.CONTENT_URI)
                    .withValues(channelData.toContentValues(inputId))
                    .build()
            }
            .chunked(100)
            .forEach {
                newElementsCount += it.size
                if (newElementsCount % 500 == 0) {
                    Timber.v("Processed $newElementsCount")
                }
                contentResolver.applyBatch(TvContract.AUTHORITY, ArrayList(it))
                    .mapNotNull { it.exception }
                    .forEach {
                        val message = "Error inserting channel data"
                        val exception = IllegalStateException(message, it)
                        Timber.e(exception, message)
                        throw exception
                    }
            }

        Timber.v("$newElementsCount rows inserted")

        val newCount = contentResolver.count(TvContract.Channels.CONTENT_URI)

        if (newCount != newElementsCount) {
            val message = "Content resolver contains $newCount rows but should be $newElementsCount"
            val e = IllegalStateException(message)
            Timber.e(e, message)
            throw e
        }

        Timber.d("Channels synced")
    }

}

private fun PlaylistChannelData.toContentValues(inputId: String): ContentValues =
    ContentValues()
        .apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_OTHER)
            put(TvContract.Channels.COLUMN_SERVICE_TYPE, TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO)
            put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID, id.toString())
            put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, id.toString())
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, name)
        }

private fun ContentResolver.count(uri: Uri): Int =
    query(
        uri,
        arrayOf(TvContract.Channels._COUNT),
        null,
        null,
        null
    )!!
        .use {
            it.let {
                it.moveToFirst()
                it.getInt(0)
            }
        }

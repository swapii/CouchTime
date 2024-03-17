package couchtime.core.tvcontract.data

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import couchtime.core.channels.model.ChannelId
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class TvContractChannelsSourceImpl @Inject constructor(
    private val context: Context,
) : TvContractChannelsSource {

    override suspend fun getChannelId(address: TvContractChannelAddress): ChannelId {
        Timber.d("Get channel id for address [$address]")
        return resolveContent {
            query(
                /* uri = */ address.address,
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
                    cursor.getString(columnIndex)
                        .toLong()
                        .let {
                            ChannelId(it)
                        }
                }
        }
    }

    override suspend fun save(channels: Sequence<ContentValues>): Int {
        Timber.d("Save channels")
        return resolveContent {
            var newElementsCount = 0
            channels
                .map {
                    ContentProviderOperation.newInsert(TvContract.Channels.CONTENT_URI)
                        .withValues(it)
                        .build()
                }
                .chunked(100)
                .forEach {
                    newElementsCount += it.size
                    if (newElementsCount % 500 == 0) {
                        Timber.v("Processed $newElementsCount")
                    }
                    applyBatch(
                        /* authority = */ TvContract.AUTHORITY,
                        /* operations = */ ArrayList(it),
                    )
                        .mapNotNull { it.exception }
                        .forEach {
                            val message = "Error inserting channel data"
                            val exception = IllegalStateException(message, it)
                            Timber.e(exception, message)
                            throw exception
                        }
                }
            Timber.v("$newElementsCount rows inserted")
            newElementsCount
        }
    }

    override suspend fun deleteAll() {
        Timber.d("Delete all channels")
        resolveContent {
            delete(
                /* url = */ TvContract.Channels.CONTENT_URI,
                /* where = */ null,
                /* selectionArgs = */ null,
            )
        }
    }

    override suspend fun count(): Int {
        Timber.d("Count channels")
        return resolveContent {
            count(TvContract.Channels.CONTENT_URI)
        }
    }

    private suspend fun <T> resolveContent(resolve: ContentResolver.() -> T): T =
        withContext(Dispatchers.IO) {
            context.contentResolver.resolve()
        }

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

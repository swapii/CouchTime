package couchtime.core.tvcontract.data

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import androidx.core.database.getStringOrNull
import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.tvcontract.domain.model.TvContractChannelId
import couchtime.core.tvcontract.domain.model.asTvContractChannelId
import couchtime.core.tvcontract.domain.model.asTvContractDisplayNumber
import couchtime.core.tvcontract.domain.model.toUri
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class TvContractChannelsSourceImpl @Inject constructor(
    private val context: Context,
) : TvContractChannelsSource {

    override suspend fun getAll(): List<TvContractChannel> {
        Timber.d("Get all channels")
        return resolveContent {
            query(
                /* uri = */ TvContract.Channels.CONTENT_URI,
                /* projection = */
                arrayOf(
                    TvContract.Channels._ID,
                    TvContract.Channels.COLUMN_INPUT_ID,
                    TvContract.Channels.COLUMN_TYPE,
                    TvContract.Channels.COLUMN_SERVICE_TYPE,
                    TvContract.Channels.COLUMN_DISPLAY_NUMBER,
                    TvContract.Channels.COLUMN_DISPLAY_NAME,
                    TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID,
                ),
                /* queryArgs = */ null,
                /* cancellationSignal = */ null,
            )!!
                .use { cursor ->

                    val idColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels._ID)

                    val inputIdColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_INPUT_ID)

                    val typeColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_TYPE)

                    val serviceTypeColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_SERVICE_TYPE)

                    val displayNumberColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NUMBER)

                    val displayNameColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NAME)

                    val internalProviderIdColumnIndex: Int =
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID)

                    cursor.iterate {
                        TvContractChannel(
                            id = getLong(idColumnIndex).asTvContractChannelId(),
                            inputId = getString(inputIdColumnIndex),
                            type = getString(typeColumnIndex),
                            serviceType = getString(serviceTypeColumnIndex),
                            displayNumber = getStringOrNull(displayNumberColumnIndex)?.asTvContractDisplayNumber(),
                            displayName = getStringOrNull(displayNameColumnIndex),
                            internalProviderId = getStringOrNull(internalProviderIdColumnIndex),
                        )
                    }
                }
        }
    }

    override suspend fun count(): Int {
        Timber.d("Count channels")
        return resolveContent {
            count(TvContract.Channels.CONTENT_URI)
        }
    }

    override suspend fun getChannelInternalProviderId(address: TvContractChannelAddress): String {
        Timber.d("Get channel internal provider id for address [$address]")
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
                        cursor.requireColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID)
                    cursor.moveToFirst()
                    cursor.getString(columnIndex)
                }
        }
    }

    override suspend fun insert(channels: List<TvContractChannel>): Int {
        Timber.d("Insert channels")

        channels
            .forEach {
                require(it.id == null) { "Channel to insert can't contain ID [$it]" }
            }

        return resolveContent {
            var newElementsCount = 0
            channels
                .map {
                    ContentProviderOperation.newInsert(TvContract.Channels.CONTENT_URI)
                        .withValues(it.toContentValues())
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

    override suspend fun update(channels: List<TvContractChannel>): Int {
        Timber.d("Update channels")
        return resolveContent {
            channels
                .forEach {
                    update(
                        it.id!!.toUri(),
                        it.toContentValues(),
                        null
                    )
                }
            0
        }
    }

    override suspend fun delete(ids: Set<TvContractChannelId>) {
        Timber.d("Delete channels $ids")
        resolveContent {
            ids
                .map { it.toUri() }
                .forEach {
                    delete(
                        /* url = */ it,
                        /* where = */ null,
                        /* selectionArgs = */ null,
                    )
                }
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

    private suspend fun <T> resolveContent(resolve: suspend ContentResolver.() -> T): T =
        withContext(Dispatchers.IO) {
            context.contentResolver.resolve()
        }

}

private fun TvContractChannel.toContentValues(): ContentValues =
    ContentValues()
        .apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_TYPE, type)
            put(TvContract.Channels.COLUMN_SERVICE_TYPE, serviceType)
            displayNumber?.let {
                put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, it.value)
            }
            displayName?.let {
                put(TvContract.Channels.COLUMN_DISPLAY_NAME, it)
            }
            internalProviderId?.let {
                put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID, it)
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
            it.moveToFirst()
            it.getInt(0)
        }

private fun Cursor.requireColumnIndex(columnName: String): Int =
    getColumnIndex(columnName)
        .also { check(it >= 0) }

private fun <T> Cursor.iterate(item: Cursor.() -> T): List<T> =
    buildList {
        if (!moveToFirst()) {
            return@buildList
        }
        do {
            add(item())
        } while (moveToNext())
    }

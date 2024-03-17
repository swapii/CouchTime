package couchtime.feature.sync

import android.content.ContentValues
import android.media.tv.TvContract
import couchtime.core.channels.model.PlaylistChannel
import couchtime.core.channels.source.PlaylistChannelsSource
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import timber.log.Timber
import javax.inject.Inject

internal class SyncChannels @Inject constructor(
    private val playlistChannelsSource: PlaylistChannelsSource,
    private val tvContractChannelsSource: TvContractChannelsSource,
) {

    suspend operator fun invoke(inputId: String) {
        Timber.d("Sync channels")

        val count: Int =
            tvContractChannelsSource.count()

        if (count > 0) {
            Timber.d("Deleting $count existing rows")
            tvContractChannelsSource.deleteAll()
        }

        val channelsSaved: Int =
            tvContractChannelsSource.save(
                playlistChannelsSource.readAll()
                    .map { channelData ->
                        channelData.toContentValues(inputId)
                    }
            )

        val newCount = tvContractChannelsSource.count()

        if (newCount != channelsSaved) {
            val message = "Content resolver contains $newCount rows but should be $channelsSaved"
            val e = IllegalStateException(message)
            Timber.e(e, message)
            throw e
        }

        Timber.d("Channels synced")
    }

}

private fun PlaylistChannel.toContentValues(inputId: String): ContentValues =
    ContentValues()
        .apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_OTHER)
            put(TvContract.Channels.COLUMN_SERVICE_TYPE, TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO)
            put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_ID, id.id.toString())
            put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, id.id.toString())
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, name)
        }

package couchtime.feature.sync

import android.media.tv.TvContract
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractDisplayNumber
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelDisplayNumber
import couchtime.feature.channel.domain.source.LocalChannelsSource
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class SyncChannels @Inject constructor(
    private val googleSheetChannelsSource: GoogleSheetChannelsSource,
    private val localChannelsSource: LocalChannelsSource,
    private val tvContractChannelsSource: TvContractChannelsSource,
) {

    suspend operator fun invoke(inputId: String) {
        Timber.d("Sync channels")

        localChannelsSource.save(
            googleSheetChannelsSource.readAll().asFlow()
                .map(GoogleSheetChannel::toChannel)
        )

        val count: Int =
            tvContractChannelsSource.count()

        if (count > 0) {
            Timber.d("Deleting $count existing rows")
            tvContractChannelsSource.deleteAll()
        }

        val channelsSaved: Int =
            tvContractChannelsSource.save(
                localChannelsSource.readAll()
                    .map { it.tvContractChannel(inputId) },
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

private fun GoogleSheetChannel.toChannel(): Channel =
    Channel(
        displayNumber = ChannelDisplayNumber(displayNumber.value),
        name = name,
        address = address,
    )

private fun Channel.tvContractChannel(inputId: String): TvContractChannel =
    TvContractChannel(
        inputId = inputId,
        type = TvContract.Channels.TYPE_OTHER,
        serviceType = TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO,
        displayNumber = TvContractDisplayNumber(displayNumber.value),
        name = name,
    )

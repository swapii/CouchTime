package couchtime.feature.sync

import android.media.tv.TvContract
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelId
import couchtime.core.tvcontract.domain.model.TvContractDisplayNumber
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelDisplayNumber
import couchtime.feature.channel.domain.model.ChannelId
import couchtime.feature.channel.domain.model.asChannelId
import couchtime.feature.channel.domain.source.LocalChannelsSource
import timber.log.Timber
import javax.inject.Inject

internal class SyncChannels @Inject constructor(
    private val googleSheetChannelsSource: GoogleSheetChannelsSource,
    private val localChannelsSource: LocalChannelsSource,
    private val tvContractChannelsSource: TvContractChannelsSource,
) {

    suspend operator fun invoke(inputId: String) {
        Timber.d("Sync channels")

        val allChannels: List<Channel> =
            googleSheetChannelsSource.getAll()
                .map(GoogleSheetChannel::toChannel)

        val channelIds: Set<ChannelId> =
            allChannels
                .map { it.id }
                .ensureSet()

        localChannelsSource.save(allChannels)

        Timber.v("All channels $channelIds")

        tvContractChannelsSource.getAll()
            .filter { it.internalProviderId?.asChannelId() !in channelIds }
            .map { it.id!! }
            .ensureSet()
            .let { ids: Set<TvContractChannelId> ->
                Timber.v("Channels to delete from TV contract $ids")
                tvContractChannelsSource.delete(ids)
            }

        val currentTvContractChannels: List<TvContractChannel> =
            tvContractChannelsSource.getAll()

        Timber.v("Channels in TV contract after clear $currentTvContractChannels")

        val channelIdsToUpdate: Set<ChannelId> =
            currentTvContractChannels
                .mapNotNull { it.internalProviderId?.asChannelId() }
                .filter { it in channelIds }
                .toSet()

        val channelsIdsToInsert: Set<ChannelId> =
            channelIds - channelIdsToUpdate

        allChannels
            .filter { it.id in channelsIdsToInsert }
            .map { it.toTvContractChannel(inputId) }
            .let { channels: List<TvContractChannel> ->
                Timber.v("Channels to add to TV contract $channels")
                tvContractChannelsSource.insert(channels)
            }

        currentTvContractChannels
            .filter { it.internalProviderId?.asChannelId() in channelIdsToUpdate }
            .map { tvContractChannel ->
                val updatedChannel: TvContractChannel =
                    allChannels
                        .first { it.id == tvContractChannel.internalProviderId!!.asChannelId() }
                        .toTvContractChannel(inputId)
                tvContractChannel.copy(
                    displayNumber = updatedChannel.displayNumber,
                    displayName = updatedChannel.displayName,
                )
            }
            .let { channels ->
                Timber.v("Channels to update in TV contract $channels")
                tvContractChannelsSource.update(channels)
            }

        Timber.d("Channels synced")
    }

}

private fun GoogleSheetChannel.toChannel(): Channel =
    Channel(
        id = id.asChannelId(),
        displayNumber = ChannelDisplayNumber(displayNumber.value),
        name = name,
        address = address,
    )

private fun Channel.toTvContractChannel(inputId: String): TvContractChannel =
    TvContractChannel(
        inputId = inputId,
        type = TvContract.Channels.TYPE_OTHER,
        serviceType = TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO,
        displayNumber = TvContractDisplayNumber(displayNumber.value),
        displayName = name,
        internalProviderId = id.value,
    )

private fun <T> Collection<T>.ensureSet(): Set<T> =
    buildSet {
        this@ensureSet.forEach {
            require(add(it))
        }
    }

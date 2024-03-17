package couchtime.feature.sync

import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject

internal class SyncChannels @Inject constructor(
    private val googleSheetChannelsSource: GoogleSheetChannelsSource,
    private val channelsDatabaseSource: ChannelsDatabaseSource,
    private val tvContractChannelsSource: TvContractChannelsSource,
) {

    suspend operator fun invoke(inputId: String) {
        Timber.d("Sync channels")

        channelsDatabaseSource.deleteAll()

        channelsDatabaseSource.save(
            googleSheetChannelsSource.readAll().asFlow()
        )

        val count: Int =
            tvContractChannelsSource.count()

        if (count > 0) {
            Timber.d("Deleting $count existing rows")
            tvContractChannelsSource.deleteAll()
        }

        val channelsSaved: Int =
            tvContractChannelsSource.save(
                inputId = inputId,
                channels = channelsDatabaseSource.readAll(),
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

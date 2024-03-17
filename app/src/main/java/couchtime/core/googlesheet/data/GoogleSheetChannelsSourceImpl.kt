package couchtime.core.googlesheet.data

import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import timber.log.Timber
import javax.inject.Inject

internal class GoogleSheetChannelsSourceImpl @Inject constructor(
    private val googleSheetSource: GoogleSheetSource,
) : GoogleSheetChannelsSource {

    override suspend fun readAll(): List<GoogleSheetChannel> {
        Timber.d("Get playlist channels")
        return googleSheetSource.getChannels()
            .map { channel: Channel ->
                Timber.v("Channel: $channel")
                channel.toGoogleSheetChannel()
            }
    }

}

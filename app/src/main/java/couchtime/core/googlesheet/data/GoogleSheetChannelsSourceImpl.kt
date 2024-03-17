package couchtime.core.googlesheet.data

import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

internal class GoogleSheetChannelsSourceImpl @Inject constructor(
    private val googleSheetSourceProvider: Provider<GoogleSheetSource?>,
) : GoogleSheetChannelsSource {

    override suspend fun getAll(): List<GoogleSheetChannel> {
        Timber.d("Get playlist channels")

        val googleSheetSource: GoogleSheetSource =
            googleSheetSourceProvider.get()
                ?: return emptyList()

        return googleSheetSource.getChannels()
            .map { channel: Channel ->
                Timber.v("Channel: $channel")
                channel.toGoogleSheetChannel()
            }
    }

}

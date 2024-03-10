package couchtime.core.googlesheet.data

import couchtime.Settings
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.model.asGoogleSheetAddress
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

internal class GoogleSheetChannelsSourceImpl @Inject constructor(
    private val settings: Flow<Settings>,
) : GoogleSheetChannelsSource {

    override suspend fun getAll(): List<GoogleSheetChannel> {
        Timber.d("Get playlist channels")

        val googleSheetSource: GoogleSheetSource =
            settings.first()
                .googleSheetAddress
                ?.asGoogleSheetAddress()
                ?.source()
                ?: return emptyList()

        return googleSheetSource.getChannels()
            .map { channel: Channel ->
                Timber.v("Channel: $channel")
                channel.toGoogleSheetChannel()
            }
    }

}

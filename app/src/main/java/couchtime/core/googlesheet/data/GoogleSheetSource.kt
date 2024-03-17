package couchtime.core.googlesheet.data

import androidx.core.net.toUri
import com.github.theapache64.retrosheet.annotations.Read
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.model.GoogleSheetChannelDisplayNumber
import retrofit2.http.GET

interface GoogleSheetSource {

    @Read("SELECT number, name, stream")
    @GET("Channels")
    suspend fun getChannels(): List<Channel>

}

data class Channel(
    val number: String,
    val name: String,
    val stream: String,
)

internal fun Channel.toGoogleSheetChannel(): GoogleSheetChannel =
    GoogleSheetChannel(
        displayNumber = GoogleSheetChannelDisplayNumber(number),
        name = name,
        address = stream.toUri(),
    )

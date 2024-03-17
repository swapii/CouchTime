package couchtime.core.channels.data

import android.content.Context
import androidx.core.net.toUri
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheet.annotations.Read
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import couchtime.core.channels.model.ChannelId
import couchtime.core.channels.model.PlaylistChannel
import couchtime.core.channels.source.PlaylistChannelsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class PlaylistChannelsSourceImpl @Inject constructor(
    private val context: Context,
) : PlaylistChannelsSource {

    override suspend fun readAll(): Flow<PlaylistChannel> {
        Timber.d("Get playlist channels")

        val applicationId = context.packageName

        val address = File("/data/local/tmp/$applicationId.source").readText().trim()

        val retrosheetInterceptor =
            RetrosheetInterceptor.Builder()
                .setLogging(false)
                .addSheet(
                    "Channels",
                    "number", "name", "stream",
                )
                .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(retrosheetInterceptor) // and attaching interceptor
            .build()

        val moshi: Moshi =
            Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(address)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val channelsSource = retrofit.create(ChannelsSource::class.java)

        val channels = channelsSource.getChannels()

        return channels
            .map { channel: Channel ->
                Timber.v("Channel: $channel")
                channel.toPlaylistChannel()
            }
            .asFlow()
    }

}

interface ChannelsSource {

    @Read("SELECT number, name, stream")
    @GET("Channels")
    suspend fun getChannels(): List<Channel>

}

data class Channel(
    val number: Int,
    val name: String,
    val stream: String,
)

private fun Channel.toPlaylistChannel(): PlaylistChannel =
    PlaylistChannel(
        id = ChannelId(number.toLong()),
        name = name,
        group = "",
        address = stream.toUri(),
    )

package couchtime.core.googlesheet.data

import androidx.core.net.toUri
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheet.annotations.Read
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import couchtime.core.googlesheet.domain.model.GoogleSheetAddress
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.model.GoogleSheetChannelDisplayNumber
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface GoogleSheetSource {

    @Read("SELECT id, number, name, stream")
    @GET("Channels")
    suspend fun getChannels(): List<Channel>

}

data class Channel(
    val id: String,
    val number: String,
    val name: String,
    val stream: String,
)

internal fun Channel.toGoogleSheetChannel(): GoogleSheetChannel =
    GoogleSheetChannel(
        id = id,
        displayNumber = GoogleSheetChannelDisplayNumber(number),
        name = name,
        address = stream.toUri(),
    )

inline fun <reified T> GoogleSheetAddress.source(): T =
    this.source(T::class.java)

fun <T> GoogleSheetAddress.source(clazz: Class<T>): T {

    val retrosheetInterceptor =
        RetrosheetInterceptor.Builder()
            .setLogging(false)
            .addSheet(
                "Channels",
                "id", "number", "name", "stream",
            )
            .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()

    val moshi: Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(value.toString().toHttpUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(clazz)
}

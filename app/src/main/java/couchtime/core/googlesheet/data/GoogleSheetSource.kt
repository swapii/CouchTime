package couchtime.core.googlesheet.data

import androidx.core.net.toUri
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.github.theapache64.retrosheet.annotations.Read
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import couchtime.core.googlesheet.domain.model.GoogleSheetAddress
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.model.GoogleSheetChannelDisplayNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET

interface GoogleSheetSource {

    @Read("SELECT id, number, name, stream")
    @GET("Channels")
    suspend fun getChannels(): List<Channel>

}

@Serializable
data class Channel(

    @SerialName("id")
    val id: String,

    @SerialName("number")
    val number: String,

    @SerialName("name")
    val name: String,

    @SerialName("stream")
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

    val url: HttpUrl =
        this.value.toString().toHttpUrl()

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(jsonConverterFactory)
        .build()

    return retrofit.create(clazz)
}

@Suppress("JSON_FORMAT_REDUNDANT")
private val jsonConverterFactory: Converter.Factory
        by lazy {
            Json {
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        }

private val okHttpClient: OkHttpClient
        by lazy {
            val retrosheetInterceptor =
                RetrosheetInterceptor.Builder()
                    .setLogging(false)
                    .addSheet(
                        "Channels",
                        "id", "number", "name", "stream",
                    )
                    .build()

            OkHttpClient.Builder()
                .addInterceptor(retrosheetInterceptor)
                .build()
        }

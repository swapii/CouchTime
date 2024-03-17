package couchtime.core.googlesheet.data

import android.content.Context
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
internal class GoogleSheetCoreDataModule {

    @Provides
    fun channelsSource(x: GoogleSheetChannelsSourceImpl): GoogleSheetChannelsSource = x

    @Provides
    fun googleSheetSource(context: Context): GoogleSheetSource {

        val applicationId = context.packageName

        val address: HttpUrl =
            File("/data/local/tmp/$applicationId.source")
                .readText()
                .trim()
                .toHttpUrl()

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

        return retrofit.create(GoogleSheetSource::class.java)
    }

}

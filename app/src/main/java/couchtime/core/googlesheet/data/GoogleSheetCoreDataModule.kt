package couchtime.core.googlesheet.data

import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import com.github.theapache64.retrosheet.RetrosheetInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import couchtime.Settings
import couchtime.core.googlesheet.domain.model.GoogleSheetAddress
import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoogleSheetCoreDataModule {

    @Provides
    @Singleton
    fun googleSheetAddress(settingsStore: DataStore<Settings>): StateFlow<GoogleSheetAddress?> =
        settingsStore.data
            .mapNotNull { it.googleSheetAddress }
            .map { GoogleSheetAddress(it.toUri()) }
            .stateIn(GlobalScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    @Provides
    fun channelsSource(x: GoogleSheetChannelsSourceImpl): GoogleSheetChannelsSource = x

    @Provides
    fun googleSheetSource(googleSheetAddress: StateFlow<GoogleSheetAddress?>): GoogleSheetSource? {

        val sheetAddress: GoogleSheetAddress = googleSheetAddress.value
            ?: return null

        val retrosheetInterceptor =
            RetrosheetInterceptor.Builder()
                .setLogging(false)
                .addSheet(
                    "Channels",
                    "id", "number", "name", "stream",
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
            .baseUrl(sheetAddress.value.toString().toHttpUrl())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(GoogleSheetSource::class.java)
    }

}

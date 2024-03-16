package couchtime.sync

import couchtime.Settings
import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelId
import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import couchtime.tvcontract.domain.action.UpdateTvContractChannelLogo
import couchtime.xmltv.XmlTvHandler
import couchtime.xmltv.parseXmlTvGzip
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class SyncElectronicProgramGuide @Inject constructor(
    private val settings: Flow<Settings>,
    private val updateTvContractChannelLogo: UpdateTvContractChannelLogo,
    private val tvContractChannelsSource: TvContractChannelsSource,
) {

    private val okHttpClient: OkHttpClient
            by lazy {
                OkHttpClient.Builder()
                    .build()
            }

    suspend operator fun invoke() {
        Timber.d("Sync electronic program guide")
        withContext(Dispatchers.Default) {

            val channels: Map<String, TvContractChannel> =
                tvContractChannelsSource.getAll()
                    .associateBy { it.displayName!! }

            val updateChannelLogoTasks: MutableList<Deferred<Unit>> = mutableListOf()

            requestEpg(
                handleChannel = { channel: XmlTvHandler.Channel ->
                    channel.icon
                        ?.let { channelLogo ->
                            channel.displayNames.values.toList()
                                .mapNotNull { channels[it] }
                                .map { it.id!! }
                                .forEach { channelId: TvContractChannelId ->
                                    val toHttpUrl = channelLogo.toString().toHttpUrl()
                                    updateChannelLogoTasks += async {
                                        updateTvContractChannelLogo(channelId, toHttpUrl)
                                    }
                                }
                        }
                },
            )

            updateChannelLogoTasks.awaitAll()

        }
        Timber.v("Electronic program guide synced")
    }

    private suspend fun requestEpg(
        handleChannel: (XmlTvHandler.Channel) -> Unit,
    ) {

        val epgAddress: HttpUrl =
            settings.first()
                .epgAddress!!
                .toHttpUrl()

        withContext(Dispatchers.IO) {
            okHttpClient
                .newCall(
                    Request.Builder()
                        .get()
                        .url(epgAddress)
                        .build()
                )
                .execute()
                .use { response: Response ->
                    check(response.isSuccessful)
                    checkNotNull(response.body)
                        .byteStream()
                        .use { inputStream ->
                            inputStream.parseXmlTvGzip(
                                handleChannel = handleChannel,
                            )
                        }
                }
        }

    }

}

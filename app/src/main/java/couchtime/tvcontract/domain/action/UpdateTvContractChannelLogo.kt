package couchtime.tvcontract.domain.action

import android.content.ContentResolver
import android.content.Context
import android.media.tv.TvContract
import android.net.Uri
import couchtime.core.tvcontract.domain.model.TvContractChannelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class UpdateTvContractChannelLogo @Inject constructor(
    private val context: Context,
) {

    private val okHttpClient: OkHttpClient
            by lazy {
                OkHttpClient.Builder()
                    .build()
            }

    suspend operator fun invoke(
        channelId: TvContractChannelId,
        logoAddress: HttpUrl,
    ) {
        Timber.d("Update logo for channel ${channelId.value} from [$logoAddress]")
        withContext(Dispatchers.IO) {
            okHttpClient
                .newCall(
                    Request.Builder()
                        .get()
                        .url(logoAddress)
                        .build()
                )
                .execute()
                .use { response ->
                    response.body!!.byteStream()
                        .use { inputStream ->
                            context.contentResolver
                                .updateTvContractChannelLogo(
                                    channelId,
                                    inputStream
                                )
                        }
                }
        }
    }

}

private fun ContentResolver.updateTvContractChannelLogo(
    channelId: TvContractChannelId,
    inputStream: InputStream,
) {
    createTvContractChannelLogoOutputStream(channelId)!!
        .buffered()
        .use { outputStream ->
            inputStream.copyTo(outputStream)
        }
}

private fun ContentResolver.createTvContractChannelLogoOutputStream(
    channelId: TvContractChannelId,
): FileOutputStream? =
    openAssetFileDescriptor(channelId.channelLogoAddress, "rw")
        ?.createOutputStream()

private val TvContractChannelId.channelLogoAddress: Uri
    get() = TvContract.buildChannelLogoUri(value)

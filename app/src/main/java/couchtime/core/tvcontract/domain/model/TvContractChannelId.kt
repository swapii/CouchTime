package couchtime.core.tvcontract.domain.model

import android.media.tv.TvContract
import android.net.Uri
import androidx.core.net.toUri

@JvmInline
value class TvContractChannelId(val value: Long)

fun TvContractChannelId.toUri(): Uri =
    (TvContract.Channels.CONTENT_URI.toString() + "/" + value).toUri()

fun Long.asTvContractChannelId() = TvContractChannelId(this)

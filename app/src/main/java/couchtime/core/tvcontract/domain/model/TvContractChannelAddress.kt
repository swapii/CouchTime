package couchtime.core.tvcontract.domain.model

import android.net.Uri

/**
 * Address of channel in TvContract content provider.
 *
 * See [android.media.tv.TvContract.Channels.CONTENT_URI].
 */
@JvmInline
value class TvContractChannelAddress(val address: Uri)

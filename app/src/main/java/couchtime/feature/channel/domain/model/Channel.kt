package couchtime.feature.channel.domain.model

import android.net.Uri

data class Channel(
    val displayNumber: ChannelDisplayNumber,
    val name: String,
    val address: Uri,
)

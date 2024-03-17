package couchtime.feature.channel.domain.model

import android.net.Uri
import couchtime.feature.channel.domain.model.ChannelId

data class Channel(
    val id: ChannelId,
    val displayNumber: ChannelDisplayNumber,
    val name: String,
    val address: Uri,
)

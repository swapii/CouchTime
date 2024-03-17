package couchtime.core.channels.model

import android.net.Uri

data class PlaylistChannel(
    val id: ChannelId,
    val name: String,
    val group: String,
    val address: Uri,
)

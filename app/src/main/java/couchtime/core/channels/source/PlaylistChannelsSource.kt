package couchtime.core.channels.source

import couchtime.core.channels.model.PlaylistChannel

interface PlaylistChannelsSource {

    suspend fun readAll(): Sequence<PlaylistChannel>

}

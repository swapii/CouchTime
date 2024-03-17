package couchtime.core.channels.source

import couchtime.core.channels.model.PlaylistChannel
import kotlinx.coroutines.flow.Flow

interface PlaylistChannelsSource {

    suspend fun readAll(): Flow<PlaylistChannel>

}

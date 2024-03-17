package couchtime.feature.sync

import couchtime.core.channels.model.ChannelId
import couchtime.core.channels.model.PlaylistChannel
import couchtime.core.coroutines.chunked
import couchtime.core.database.entity.PlaylistChannelDao
import couchtime.core.database.entity.PlaylistChannelDatabaseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistChannelsDatabaseSource @Inject constructor(
    private val playlistChannelDao: PlaylistChannelDao,
) {

    suspend fun getChannel(channelId: ChannelId): PlaylistChannel? =
        playlistChannelDao
            .getChannel(channelId.value)
            ?.toDomainModel()

    fun readAll(): Flow<PlaylistChannel> =
        flow {
            playlistChannelDao.getAll()
                .forEach {
                    emit(it)
                }
        }
            .map(PlaylistChannelDatabaseEntity::toDomainModel)

    suspend fun save(channels: Flow<PlaylistChannel>): Int {
        var count = 0
        channels
            .map(PlaylistChannel::toDatabaseEntity)
            .chunked(100)
            .collect { entities ->
                playlistChannelDao.save(entities)
                count += entities.size
            }
        return count
    }

    suspend fun deleteAll() {
        playlistChannelDao.deleteAll()
    }

}

private fun PlaylistChannel.toDatabaseEntity() =
    PlaylistChannelDatabaseEntity(
        id = id.value,
        name = name,
        group = group,
        address = address,
    )

private fun PlaylistChannelDatabaseEntity.toDomainModel() =
    PlaylistChannel(
        id = ChannelId(id),
        name = name,
        group = group,
        address = address,
    )

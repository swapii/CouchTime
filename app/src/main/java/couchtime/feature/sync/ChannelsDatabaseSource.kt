package couchtime.feature.sync

import couchtime.core.coroutines.chunked
import couchtime.core.database.entity.ChannelDao
import couchtime.core.database.entity.ChannelDatabaseEntity
import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelDisplayNumber
import couchtime.feature.channel.domain.source.LocalChannelsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalChannelsSourceImpl @Inject constructor(
    private val channelDao: ChannelDao,
) : LocalChannelsSource {

    override suspend fun getChannel(channelNumber: ChannelDisplayNumber): Channel =
        channelDao
            .getChannel(channelNumber.value)
            .toDomainModel()

    override fun readAll(): Flow<Channel> =
        flow {
            channelDao.getAll()
                .forEach {
                    emit(it)
                }
        }
            .map(ChannelDatabaseEntity::toDomainModel)

    override suspend fun save(channels: Flow<Channel>): Int {
        var count = 0
        channels
            .map(Channel::toDatabaseEntity)
            .chunked(100)
            .collect { entities ->
                channelDao.save(entities)
                count += entities.size
            }
        return count
    }

    override suspend fun deleteAll() {
        channelDao.deleteAll()
    }

}

private fun Channel.toDatabaseEntity() =
    ChannelDatabaseEntity(
        number = displayNumber.value,
        name = name,
        address = address,
    )

private fun ChannelDatabaseEntity.toDomainModel() =
    Channel(
        displayNumber = ChannelDisplayNumber(number),
        name = name,
        address = address,
    )

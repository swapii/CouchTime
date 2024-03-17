package couchtime.feature.sync

import couchtime.core.database.WithDatabaseTransaction
import couchtime.core.database.entity.ChannelDao
import couchtime.core.database.entity.ChannelDatabaseEntity
import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelDisplayNumber
import couchtime.feature.channel.domain.model.ChannelId
import couchtime.feature.channel.domain.model.asChannelId
import couchtime.feature.channel.domain.source.LocalChannelsSource
import javax.inject.Inject

class LocalChannelsSourceImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val withDatabaseTransaction: WithDatabaseTransaction,
) : LocalChannelsSource {

    override suspend fun getChannel(id: ChannelId): Channel =
        channelDao
            .getChannel(id.value)
            .toDomainModel()

    override suspend fun save(channels: List<Channel>): Int {
        var count = 0
        withDatabaseTransaction {
            channelDao.deleteAll()
            channels
                .map(Channel::toDatabaseEntity)
                .chunked(100)
                .forEach { entities ->
                    channelDao.save(entities)
                    count += entities.size
                }
        }
        return count
    }

}

private fun Channel.toDatabaseEntity() =
    ChannelDatabaseEntity(
        id = id.value,
        number = displayNumber.value,
        name = name,
        address = address,
    )

private fun ChannelDatabaseEntity.toDomainModel() =
    Channel(
        id = id.asChannelId(),
        displayNumber = ChannelDisplayNumber(number),
        name = name,
        address = address,
    )

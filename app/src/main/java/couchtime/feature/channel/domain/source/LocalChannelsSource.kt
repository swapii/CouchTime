package couchtime.feature.channel.domain.source

import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelDisplayNumber
import kotlinx.coroutines.flow.Flow

interface LocalChannelsSource {

    fun readAll(): Flow<Channel>

    suspend fun getChannel(channelNumber: ChannelDisplayNumber): Channel

    suspend fun save(channels: Flow<Channel>): Int

    suspend fun deleteAll()

}

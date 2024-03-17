package couchtime.feature.channel.domain.source

import couchtime.feature.channel.domain.model.Channel
import couchtime.feature.channel.domain.model.ChannelId

interface LocalChannelsSource {

    suspend fun getChannel(id: ChannelId): Channel

    suspend fun save(channels: List<Channel>): Int

}

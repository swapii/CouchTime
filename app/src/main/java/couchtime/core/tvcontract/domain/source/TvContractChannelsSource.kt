package couchtime.core.tvcontract.domain.source

import couchtime.core.channels.model.ChannelId
import couchtime.core.channels.model.PlaylistChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import kotlinx.coroutines.flow.Flow

interface TvContractChannelsSource {

    suspend fun getChannelId(address: TvContractChannelAddress): ChannelId

    suspend fun save(inputId: String, channels: Flow<PlaylistChannel>): Int

    suspend fun deleteAll()

    suspend fun count(): Int

}

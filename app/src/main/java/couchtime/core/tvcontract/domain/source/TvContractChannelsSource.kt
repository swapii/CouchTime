package couchtime.core.tvcontract.domain.source

import android.content.ContentValues
import couchtime.core.channels.model.ChannelId
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress

interface TvContractChannelsSource {

    suspend fun getChannelId(address: TvContractChannelAddress): ChannelId

    suspend fun save(channels: Sequence<ContentValues>): Int

    suspend fun deleteAll()

    suspend fun count(): Int

}

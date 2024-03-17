package couchtime.core.tvcontract.domain.source

import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.tvcontract.domain.model.TvContractChannelId

interface TvContractChannelsSource {

    suspend fun getAll(): List<TvContractChannel>

    suspend fun count(): Int

    suspend fun getChannelInternalProviderId(address: TvContractChannelAddress): String

    suspend fun insert(channels: List<TvContractChannel>): Int

    suspend fun update(channels: List<TvContractChannel>): Int

    suspend fun delete(ids: Set<TvContractChannelId>)

    suspend fun deleteAll()

}

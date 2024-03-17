package couchtime.core.tvcontract.domain.source

import couchtime.core.tvcontract.domain.model.TvContractChannel
import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.tvcontract.domain.model.TvContractDisplayNumber
import kotlinx.coroutines.flow.Flow

interface TvContractChannelsSource {

    suspend fun getChannelDisplayNumber(address: TvContractChannelAddress): TvContractDisplayNumber

    suspend fun save(channels: Flow<TvContractChannel>): Int

    suspend fun deleteAll()

    suspend fun count(): Int

}

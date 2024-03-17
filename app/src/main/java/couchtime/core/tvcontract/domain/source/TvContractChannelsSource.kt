package couchtime.core.tvcontract.domain.source

import couchtime.core.tvcontract.domain.model.TvContractChannelAddress
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.tvcontract.domain.model.TvContractDisplayNumber
import kotlinx.coroutines.flow.Flow

interface TvContractChannelsSource {

    suspend fun getChannelDisplayNumber(address: TvContractChannelAddress): TvContractDisplayNumber

    suspend fun save(inputId: String, channels: Flow<GoogleSheetChannel>): Int

    suspend fun deleteAll()

    suspend fun count(): Int

}

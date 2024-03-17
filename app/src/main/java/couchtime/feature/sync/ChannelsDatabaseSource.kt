package couchtime.feature.sync

import couchtime.core.coroutines.chunked
import couchtime.core.database.entity.ChannelDao
import couchtime.core.database.entity.ChannelDatabaseEntity
import couchtime.core.googlesheet.domain.model.GoogleSheetChannel
import couchtime.core.googlesheet.domain.model.GoogleSheetChannelDisplayNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChannelsDatabaseSource @Inject constructor(
    private val channelDao: ChannelDao,
) {

    suspend fun getChannel(channelNumber: String): GoogleSheetChannel =
        channelDao
            .getChannel(channelNumber)
            .toDomainModel()

    fun readAll(): Flow<GoogleSheetChannel> =
        flow {
            channelDao.getAll()
                .forEach {
                    emit(it)
                }
        }
            .map(ChannelDatabaseEntity::toDomainModel)

    suspend fun save(channels: Flow<GoogleSheetChannel>): Int {
        var count = 0
        channels
            .map(GoogleSheetChannel::toDatabaseEntity)
            .chunked(100)
            .collect { entities ->
                channelDao.save(entities)
                count += entities.size
            }
        return count
    }

    suspend fun deleteAll() {
        channelDao.deleteAll()
    }

}

private fun GoogleSheetChannel.toDatabaseEntity() =
    ChannelDatabaseEntity(
        number = displayNumber.value,
        name = name,
        address = address,
    )

private fun ChannelDatabaseEntity.toDomainModel() =
    GoogleSheetChannel(
        displayNumber = GoogleSheetChannelDisplayNumber(number),
        name = name,
        address = address,
    )

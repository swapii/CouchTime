package couchtime.core.database.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(
    tableName = "channel",
)
data class ChannelDatabaseEntity(

    @PrimaryKey
    @ColumnInfo(
        name = "number",
    )
    val number: String,

    @ColumnInfo(
        name = "name",
    )
    val name: String,

    @ColumnInfo(
        name = "address",
    )
    val address: Uri,

)

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channel WHERE number = :channelNumber")
    suspend fun getChannel(channelNumber: String): ChannelDatabaseEntity

    @Query("SELECT * FROM channel")
    suspend fun getAll(): List<ChannelDatabaseEntity>

    @Insert
    suspend fun save(channels: List<ChannelDatabaseEntity>)

    @Query("DELETE FROM channel")
    suspend fun deleteAll()

}

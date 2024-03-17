package couchtime.core.database.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(
    tableName = "playlist_channel",
)
data class PlaylistChannelDatabaseEntity(

    @PrimaryKey
    @ColumnInfo(
        name = "id",
    )
    val id: Long,

    @ColumnInfo(
        name = "name",
    )
    val name: String,

    @ColumnInfo(
        name = "group",
    )
    val group: String,

    @ColumnInfo(
        name = "address",
    )
    val address: Uri,

)

@Dao
interface PlaylistChannelDao {

    @Query("SELECT * FROM playlist_channel WHERE id = :channelId")
    suspend fun getChannel(channelId: Long): PlaylistChannelDatabaseEntity?

    @Query("SELECT * FROM playlist_channel")
    suspend fun getAll(): List<PlaylistChannelDatabaseEntity>

    @Insert
    suspend fun save(channels: List<PlaylistChannelDatabaseEntity>)

    @Query("DELETE FROM playlist_channel")
    suspend fun deleteAll()

}

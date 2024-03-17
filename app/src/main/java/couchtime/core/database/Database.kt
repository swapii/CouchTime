package couchtime.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import couchtime.core.database.converter.UriConverter
import couchtime.core.database.entity.PlaylistChannelDao
import couchtime.core.database.entity.PlaylistChannelDatabaseEntity
import javax.inject.Singleton

@Singleton
@Database(
    version = 1,
    entities = [
        PlaylistChannelDatabaseEntity::class,
    ],
)
@TypeConverters(
    UriConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playlistChannelDao(): PlaylistChannelDao

}

package couchtime.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import couchtime.core.database.converter.UriConverter
import couchtime.core.database.entity.ChannelDao
import couchtime.core.database.entity.ChannelDatabaseEntity

@Database(
    version = 1,
    entities = [
        ChannelDatabaseEntity::class,
    ],
)
@TypeConverters(
    UriConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun channelDao(): ChannelDao

}

package couchtime.core.database

import android.content.Context
import androidx.room.Room
import couchtime.core.database.entity.ChannelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseCoreModule {

    @Provides
    @Singleton
    fun database(context: Context): AppDatabase =
        Room
            .databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "app.db",
            )
            .build()

    @Provides
    fun channelDao(database: AppDatabase): ChannelDao =
        database.channelDao()

}

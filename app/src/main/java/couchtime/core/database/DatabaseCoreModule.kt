package couchtime.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseCoreModule {

    @Provides
    fun database(context: Context): AppDatabase =
        Room
            .databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "app.db",
            )
            .build()

    @Provides
    fun playlistChannelDao(database: AppDatabase) = database.playlistChannelDao()

}

package couchtime.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import couchtime.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsCoreModule {

    @Provides
    @Singleton
    fun settingsStore(context: Context): DataStore<Settings> =
        DataStoreFactory.create(
            produceFile = {
                context.dataStoreFile("settings.pb")
            },
            serializer = SettingsSerializer,
        )

    @Provides
    fun settings(settingsDataStore: DataStore<Settings>): Flow<Settings> =
        settingsDataStore.data

}

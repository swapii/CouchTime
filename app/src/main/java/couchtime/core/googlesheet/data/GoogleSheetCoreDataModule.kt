package couchtime.core.googlesheet.data

import couchtime.core.googlesheet.domain.source.GoogleSheetChannelsSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class GoogleSheetCoreDataModule {

    @Provides
    fun channelsSource(x: GoogleSheetChannelsSourceImpl): GoogleSheetChannelsSource = x

}

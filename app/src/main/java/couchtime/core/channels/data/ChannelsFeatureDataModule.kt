package couchtime.core.channels.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import couchtime.core.channels.source.PlaylistChannelsSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ChannelsFeatureDataModule {

    @Binds
    abstract fun channelsDataSource(x: PlaylistChannelsSourceImpl): PlaylistChannelsSource

}

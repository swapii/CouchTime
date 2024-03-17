package couchtime.feature.channel.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import couchtime.feature.channel.domain.source.LocalChannelsSource
import couchtime.feature.sync.LocalChannelsSourceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ChannelsFeatureDataModule {

    @Binds
    abstract fun localChannelsSource(x: LocalChannelsSourceImpl): LocalChannelsSource

}

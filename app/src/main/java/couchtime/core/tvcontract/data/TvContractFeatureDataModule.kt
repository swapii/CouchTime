package couchtime.core.tvcontract.data

import couchtime.core.tvcontract.domain.source.TvContractChannelsSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TvContractFeatureDataModule {

    @Binds
    abstract fun channelsDataSource(x: TvContractChannelsSourceImpl): TvContractChannelsSource

}

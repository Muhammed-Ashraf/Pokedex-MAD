package ashraf.pokedex.mad.core.data.di

import ashraf.pokedex.mad.core.data.repository.details.DetailsRepository
import ashraf.pokedex.mad.core.data.repository.details.DetailsRepositoryImpl
import ashraf.pokedex.mad.core.data.repository.home.HomeRepository
import ashraf.pokedex.mad.core.data.repository.home.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    @Singleton
    fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    fun bindDetailsRepository(impl: DetailsRepositoryImpl): DetailsRepository
}
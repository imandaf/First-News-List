package space.imanda.firstnewslist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import space.imanda.firstnewslist.network.api.ApiServices
import space.imanda.firstnewslist.network.repository.INewsRepository
import space.imanda.firstnewslist.network.repository.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: ApiServices
    ) = NewsRepository(remoteDataSource)

    @Singleton
    @Provides
    fun provideINewsRepository(repository: NewsRepository): INewsRepository = repository

}
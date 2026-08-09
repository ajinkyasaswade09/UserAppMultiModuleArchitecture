package com.ajinkya.data.di

import com.ajinkya.data.UsersRepository
import com.ajinkya.data.UsersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

   @Binds
   @Singleton
   abstract fun bindUsersRepository(impl: UsersRepositoryImpl): UsersRepository

   companion object {
       @Provides
       @IoDispatcher
       fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

       @Provides
       @Singleton
       @ApplicationScope
       fun provideApplicationScope(): CoroutineScope =
           CoroutineScope(SupervisorJob() + Dispatchers.Default)
   }
}
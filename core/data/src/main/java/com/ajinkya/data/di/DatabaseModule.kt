package com.ajinkya.data.di

import android.content.Context
import androidx.room.Room
import com.ajinkya.data.local.UserAppDatabase
import com.ajinkya.data.local.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

   @Provides
   @Singleton
   fun provideDatabase(@ApplicationContext context: Context): UserAppDatabase =
       Room.databaseBuilder(
           context,
           UserAppDatabase::class.java,
           UserAppDatabase.DATABASE_NAME,
       ).fallbackToDestructiveMigration(dropAllTables = true).build()

   @Provides
   @Singleton
   fun provideUsersDao(database: UserAppDatabase): UsersDao = database.usersDao()
}
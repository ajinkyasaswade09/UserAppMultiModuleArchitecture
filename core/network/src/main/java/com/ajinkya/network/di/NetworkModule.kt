package com.ajinkya.network.di

import com.ajinkya.network.UsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

   @Provides
   @Singleton
   fun provideJson(): Json = Json {
       ignoreUnknownKeys = true
       coerceInputValues = true
   }

   @Provides
   @Singleton
   fun provideOkHttpClient(): OkHttpClient {
       val logging = HttpLoggingInterceptor().apply {
           level = HttpLoggingInterceptor.Level.BODY
       }
       return OkHttpClient.Builder()
           .addInterceptor(logging)
           .build()
   }

   @Provides
   @Singleton
   fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
       val contentType = "application/json".toMediaType()
       return Retrofit.Builder()
           .baseUrl(UsersApi.BASE_URL)
           .client(client)
           .addConverterFactory(json.asConverterFactory(contentType))
           .build()
   }

   @Provides
   @Singleton
   fun provideUsersApi(retrofit: Retrofit): UsersApi = retrofit.create(UsersApi::class.java)
}

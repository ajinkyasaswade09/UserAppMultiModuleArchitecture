package com.ajinkya.network

import com.ajinkya.network.model.UserDto
import kotlinx.serialization.InternalSerializationApi
import retrofit2.http.GET

interface UsersApi {

   @OptIn(InternalSerializationApi::class)
   @GET("users")
   suspend fun getUsers(): List<UserDto>

   companion object {
       const val BASE_URL = "https://fake-json-api.mock.beeceptor.com/"
   }
}

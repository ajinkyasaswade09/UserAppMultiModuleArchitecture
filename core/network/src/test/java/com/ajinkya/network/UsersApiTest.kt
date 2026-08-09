package com.ajinkya.network

import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@OptIn(InternalSerializationApi::class)
class UsersApiTest {

   private lateinit var server: MockWebServer
   private lateinit var api: UsersApi

   @Before
   fun setUp() {
       server = MockWebServer()
       server.start()

       val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
       val contentType = "application/json".toMediaType()
       api = Retrofit.Builder()
           .baseUrl(server.url("/"))
           .addConverterFactory(json.asConverterFactory(contentType))
           .build()
           .create(UsersApi::class.java)
   }

   @After
   fun tearDown() {
       server.shutdown()
   }

   @Test
   fun `getUsers parses the response body`() = runTest {
       server.enqueue(
           MockResponse()
               .setResponseCode(200)
               .setBody(SAMPLE_JSON),
       )

       val users = api.getUsers()

       assertEquals(2, users.size)
       assertEquals(1, users[0].id)
       assertEquals("Loraine Glover", users[0].name)
       assertEquals("Rogahn LLC", users[1].company)
   }

   @Test
   fun `getUsers ignores unknown keys and missing fields`() = runTest {
       server.enqueue(
           MockResponse()
               .setResponseCode(200)
               .setBody("""[{"id":9,"name":"Only Name","unexpected":"field"}]"""),
       )

       val users = api.getUsers()

       assertEquals(1, users.size)
       assertEquals(9, users[0].id)
       assertEquals("Only Name", users[0].name)
       assertEquals(null, users[0].email)
   }

   private companion object {
       val SAMPLE_JSON = """
           [
             {
               "id": 1,
               "name": "Loraine Glover",
               "company": "Jakubowski and Sons",
               "username": "Rae.Cruickshank",
               "email": "Raymond.Stamm@yahoo.com",
               "address": "4122 Peyton Knolls",
               "zip": "30342",
               "state": "Michigan",
               "country": "Mayotte",
               "phone": "(856) 564-4434",
               "photo": "https://json-server.dev/ai-profiles/9.png"
             },
             {
               "id": 2,
               "name": "Niko",
               "company": "Rogahn LLC",
               "username": "Niko10",
               "email": "Ronaldo_Terry63@gmail.com",
               "address": "393 Janiya Run",
               "zip": "63101",
               "state": "Arkansas",
               "country": "Mayotte",
               "phone": "696.272.3131",
               "photo": "https://json-server.dev/ai-profiles/17.png"
             }
           ]
       """.trimIndent()
   }
}

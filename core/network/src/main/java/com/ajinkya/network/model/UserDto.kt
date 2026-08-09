package com.ajinkya.network.model
import com.ajinkya.model.User
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
* Wire model for the users endpoint. All fields are nullable/defaulted because the
* mock API returns randomized data and cannot be assumed to be well-formed.
*/
@InternalSerializationApi
@Serializable
data class UserDto(
   val id: Int = 0,
   val name: String? = null,
   val company: String? = null,
   val username: String? = null,
   val email: String? = null,
   val address: String? = null,
   val zip: String? = null,
   val state: String? = null,
   val country: String? = null,
   val phone: String? = null,
   val photo: String? = null,
)

@OptIn(InternalSerializationApi::class)
fun UserDto.toDomain(): User = User(
   id = id,
   name = name.orEmpty(),
   company = company.orEmpty(),
   username = username.orEmpty(),
   email = email.orEmpty(),
   address = address.orEmpty(),
   zip = zip.orEmpty(),
   state = state.orEmpty(),
   country = country.orEmpty(),
   phone = phone.orEmpty(),
   photo = photo.orEmpty(),
)

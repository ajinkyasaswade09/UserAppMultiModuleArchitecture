package com.ajinkya.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ajinkya.model.User

@Entity(tableName = "cached_users")
data class CachedUserEntity(
   @PrimaryKey val id: Int,
   val name: String,
   val company: String,
   val username: String,
   val email: String,
   val address: String,
   val zip: String,
   val state: String,
   val country: String,
   val phone: String,
   val photo: String,
)

fun CachedUserEntity.toDomain(): User = User(
   id = id,
   name = name,
   company = company,
   username = username,
   email = email,
   address = address,
   zip = zip,
   state = state,
   country = country,
   phone = phone,
   photo = photo,
)

fun User.toEntity(): CachedUserEntity = CachedUserEntity(
   id = id,
   name = name,
   company = company,
   username = username,
   email = email,
   address = address,
   zip = zip,
   state = state,
   country = country,
   phone = phone,
   photo = photo,
)


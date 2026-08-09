package com.example.userapp

import android.app.Application
import com.ajinkya.data.UsersRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UserApplication : Application() {

    // Injected so the repository (and its eagerly-started cache read) is created at
    // process start, ensuring previously stored data is loaded before the first screen.
    @Inject
    lateinit var usersRepository: UsersRepository

    override fun onCreate() {
        super.onCreate()
        // Touch the cache to kick off the eager disk read as early as possible.
        usersRepository.users
    }
}

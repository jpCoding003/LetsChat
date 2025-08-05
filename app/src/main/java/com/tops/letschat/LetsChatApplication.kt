package com.tops.letschat


import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class LetsChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Enable Firebase Realtime Database offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}

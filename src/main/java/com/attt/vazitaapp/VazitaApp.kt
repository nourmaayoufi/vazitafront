package com.attt.vazitaapp


import android.app.Application
import androidx.room.Room
import com.attt.vazitaapp.data.local.AppDatabase
import com.attt.vazitaapp.data.remote.RetrofitClient

class VazitaApp : Application() {

    companion object {
        lateinit var instance: VazitaApp
            private set

        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "vehicle_inspection_db"
        ).fallbackToDestructiveMigration().build()
    }}
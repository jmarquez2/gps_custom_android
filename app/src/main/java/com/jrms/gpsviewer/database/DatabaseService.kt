package com.jrms.gpsviewer.database

import android.content.Context
import androidx.room.Room

class DatabaseService (context : Context){
    private val db = Room.databaseBuilder(context,
                        DatabaseImp::class.java, "gps-db").build()

    val getLastLocation = db.locationDAO()::getDeviceLastLocation
    val upsertLocation = db.locationDAO()::upsertLastLocation
}
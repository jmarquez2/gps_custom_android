package com.jrms.gpsviewer.database

import android.content.Context
import androidx.room.Room
import com.jrms.gpsviewer.models.Location

class DatabaseService (context : Context){
    private val db = Room.databaseBuilder(context,
                        DatabaseImp::class.java, "gps-db").build()

    val getLastLocation = db.locationDAO()::getDeviceLastLocation
    val addLocation = db.locationDAO()::addLocation
}
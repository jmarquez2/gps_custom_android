package com.jrms.gpsviewer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jrms.gpsviewer.models.Location

@Database(entities = [Location::class], version = 1)
abstract class DatabaseImp : RoomDatabase() {
    abstract fun locationDAO() : LocationDAO
}
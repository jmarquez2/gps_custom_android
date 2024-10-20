package com.jrms.gpsviewer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location (
        @PrimaryKey(autoGenerate = false) val id : String,
        val latitude : Double = 0.0,
        val longitude : Double = 0.0,
        val timestamp : Long  = 0,
        @ColumnInfo(name = "device_id") val deviceId : String
){
        override fun toString(): String {
                return "Location(id='$id', latitude=$latitude, longitude=$longitude, timestamp=$timestamp, deviceId='$deviceId')"
        }
}
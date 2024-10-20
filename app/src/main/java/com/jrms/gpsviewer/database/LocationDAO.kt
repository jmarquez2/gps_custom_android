package com.jrms.gpsviewer.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jrms.gpsviewer.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDAO {
    @Query("select device_id, latitude, longitude, timestamp from location where device_id = :deviceId")
    suspend fun getDeviceLastLocation(deviceId : ByteArray) : Location?

    @Query("select device_id, latitude, longitude, timestamp from location order by timestamp desc")
    fun getDeviceLocations() : Flow<List<Location>>

    @Upsert
    suspend fun upsertLastLocation(location: Location)
}
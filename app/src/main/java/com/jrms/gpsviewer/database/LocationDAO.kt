package com.jrms.gpsviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jrms.gpsviewer.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDAO {
    @Query("select id, device_id, latitude, longitude, timestamp from location where device_id = :deviceId order by timestamp desc limit 1")
    suspend fun getDeviceLastLocation(deviceId : String?) : Location?

    @Query("select id, device_id, latitude, longitude, timestamp from location where device_id = :deviceId order by timestamp desc")
    fun getDeviceLocations(deviceId : String?) : Flow<List<Location>>

    @Insert
    suspend fun addLocation(location: Location)
}
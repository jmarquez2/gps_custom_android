package com.jrms.gpsviewer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jrms.gpsviewer.utils.bytesToUUID
import java.util.UUID

@Entity(tableName = "location")
data class Location (
        val latitude : Double = 0.0,
        val longitude : Double = 0.0,
        val timestamp : Long  = 0,
        @ColumnInfo(name = "device_id")
        @PrimaryKey(autoGenerate = false)
        val deviceIdBytes : ByteArray

){

        private val deviceUUID : UUID
                get() {
                        return bytesToUUID(deviceIdBytes)
                }

        override fun toString(): String {
                return "Location( deviceId=$deviceUUID, latitude=$latitude, longitude=$longitude, timestamp=$timestamp)"
        }

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Location

                if (latitude != other.latitude) return false
                if (longitude != other.longitude) return false
                if (timestamp != other.timestamp) return false
                if (!deviceIdBytes.contentEquals(other.deviceIdBytes)) return false

                return true
        }

        override fun hashCode(): Int {
                var result = latitude.hashCode()
                result = 31 * result + longitude.hashCode()
                result = 31 * result + timestamp.hashCode()
                result = 31 * result + deviceIdBytes.contentHashCode()
                return result
        }
}
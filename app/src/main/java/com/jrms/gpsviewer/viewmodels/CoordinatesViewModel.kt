package com.jrms.gpsviewer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.data.Coordinates
import com.jrms.gpsviewer.data.DATE_FORMAT
import com.jrms.gpsviewer.data.selectedDevice
import com.jrms.gpsviewer.dataStore
import com.jrms.gpsviewer.database.DatabaseService
import com.jrms.gpsviewer.models.Location
import com.jrms.gpsviewer.utils.formatDate
import com.jrms.gpsviewer.utils.parseDate
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID


class CoordinatesViewModel(private val databaseService: DatabaseService) : ViewModel() {


    private var coordinates: Coordinates? = null


    private val _coordinatesState = MutableStateFlow(coordinates)

    private val mainDispatcher = Dispatchers.IO

    val coordinatesState = _coordinatesState.asStateFlow()

    private var deviceId: String? = null


    private val getMessage = Emitter.Listener {
        receiveMessage(it)
    }

    private var ioSocket: Socket? = null

    private var connecting: Boolean = false


    private fun connectSocket() {


        viewModelScope.launch {
            withContext(mainDispatcher) {
                try {

                    connecting = true;

                    ioSocket = IO.socket(BuildConfig.API_URL)

                    ioSocket?.on(deviceId, getMessage)

                    ioSocket?.connect()

                    ioSocket?.emit("add_room_devices")


                } catch (e: Exception) {
                    Log.e("IO sockets exception", e.toString())
                } finally {
                    connecting = false;
                }
            }
        }


    }


    private fun receiveMessage(data: Array<Any>?) {
        viewModelScope.launch {
            val serializedData = data?.get(0) as JSONObject

            val latitude: Double;
            val longitude: Double


            try {
                latitude = serializedData.getDouble("latitude")
                longitude = serializedData.getDouble("longitude")

                updateCoordinates(latitude, longitude)

            } catch (e: Exception) {
                Log.e("Error serializing JSON", e.toString())

            }

        }
    }

    private fun updateCoordinates(latitude: Double, longitude: Double, lastUpdate: String? = null) {

        val date = Date()
        val dateString = lastUpdate ?: formatDate(date, Locale.getDefault())

        _coordinatesState.update {
            this.coordinates = it?.copy(
                latitude = latitude,
                longitude = longitude,
                date = dateString

            )
                ?: Coordinates(
                    latitude = latitude,
                    longitude = longitude,
                    date = dateString
                )

            coordinates

        }
    }

    fun startSocket() {
        if ((ioSocket == null || (ioSocket?.connected() == false)) && !connecting) {
            connectSocket()
        }
    }

    fun setCoordinatesAndConnect(previousID: String?) {
        viewModelScope.launch {
            withContext(mainDispatcher) {

                val location = getLastLocation(previousID)


                val date = if(location?.timestamp != null){

                formatDate(
                    Date(location.timestamp), Locale.getDefault()
                )}else{
                    ""
                }

                updateCoordinates(
                    location?.latitude ?: 0.0, location?.longitude ?: 0.0, date
                )


                if (previousID != deviceId) {
                    deviceId = previousID
                    disconnectSocket()
                    startSocket()
                }
            }
        }
    }


    fun disconnectSocket(saveLastToDb: Boolean = false) {
        viewModelScope.launch {
            withContext(mainDispatcher) {
                ioSocket?.close()
                ioSocket?.disconnect()
                ioSocket = null

                if (saveLastToDb) {

                    if (coordinates != null) {
                        val timestamp = parseDate(coordinates!!.date, Locale.getDefault())!!.time
                        val location = Location(
                            UUID.randomUUID().toString(),
                            coordinates!!.latitude, coordinates!!.longitude, timestamp, deviceId!!
                        )
                        databaseService.addLocation(location)
                        Log.i("LOCATION", "Location saved $location")
                    }

                }
            }
        }

    }


    private suspend fun getLastLocation(id : String?): Location? {
        return this.databaseService.getLastLocation(id);
    }


}
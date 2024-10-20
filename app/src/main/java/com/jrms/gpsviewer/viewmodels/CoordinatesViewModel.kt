package com.jrms.gpsviewer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.data.Coordinates
import com.jrms.gpsviewer.database.DatabaseService
import com.jrms.gpsviewer.models.Location
import com.jrms.gpsviewer.utils.formatDate
import com.jrms.gpsviewer.utils.parseDate
import com.jrms.gpsviewer.utils.uUIDToBytes
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale


class CoordinatesViewModel(private val databaseService: DatabaseService) : ViewModel() {


    private var coordinates: Coordinates? = null


    private val _coordinatesState = MutableStateFlow(coordinates)

    private val mainDispatcher = Dispatchers.IO

    val coordinatesState = _coordinatesState.asStateFlow()

    private var deviceId: String? = null

    private var lastLocation : Location? = null


    private val getMessage = Emitter.Listener {
        receiveMessage(it)
    }

    private var ioSocket: Socket? = null

    private var connecting: Boolean = false



    fun setCoordinatesAndConnect(previousID: String?) {
        viewModelScope.launch {
            withContext(mainDispatcher) {

                val location = getLastLocation(previousID)


                if(location != null){
                    updateCoordinates(
                        location.latitude, location.longitude,  formatDate(
                            Date(location.timestamp), Locale.getDefault()
                        )
                    )
                }


                lastLocation = location


                if (previousID != deviceId) {
                    deviceId = previousID
                    disconnectSocket()
                    startSocket()
                }
            }
        }
    }

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
            withContext(mainDispatcher) {
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
    }

    private suspend fun updateCoordinates(latitude: Double, longitude: Double, lastUpdate: String? = null) {

        val date = Date()
        val dateString = lastUpdate ?: formatDate(date, Locale.getDefault())


        if(lastLocation == null){
           try{
               databaseService.upsertLocation(Location(latitude, longitude, date.time, uUIDToBytes(deviceId!!)))
           }catch (e : Exception){
               Log.e("Save location", "Error saving last location : $e")
           }
        }

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




    fun disconnectSocket() {
        viewModelScope.launch {
            withContext(mainDispatcher) {
                ioSocket?.close()
                ioSocket?.disconnect()
                ioSocket = null



                if (coordinates != null) {
                    val timestamp = parseDate(coordinates!!.date, Locale.getDefault())!!.time
                    val location = Location(
                        coordinates!!.latitude, coordinates!!.longitude, timestamp, uUIDToBytes(deviceId!!)
                    )
                    databaseService.upsertLocation(location)
                    Log.i("LOCATION", "Location saved $location")
                }


            }
        }

    }


    private suspend fun getLastLocation(id : String?): Location? {
        return if(id == null){
            null
        }else{
            this.databaseService.getLastLocation(uUIDToBytes(id));
        }

    }


}
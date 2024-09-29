package com.jrms.gpsviewer.viewmodels

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.data.Coordinates
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CoordinatesViewModel() : ViewModel(){



    private val formatter = SimpleDateFormat.getDateTimeInstance()
    private val _coordinatesState = MutableStateFlow<Coordinates?>(null)

    val coordinatesState  = _coordinatesState.asStateFlow()

    var deviceId : String = ""



    private val getMessage = Emitter.Listener {
        receiveMessage(it)
    }

    private var ioSocket: Socket? = null

    private var connecting : Boolean = false



    private fun connectSocket(){

        viewModelScope.launch {
            withContext(Dispatchers.IO){

            }
        }

        try {

            connecting = true;

            ioSocket = IO.socket(BuildConfig.API_URL)

            ioSocket?.on(deviceId, getMessage)

            ioSocket?.connect()

            ioSocket?.emit("add_room_devices")


        }catch (e : Exception){
            Log.e("IO sockets exception", e.toString())
        }finally {
            connecting = false;
        }
    }





    private fun receiveMessage(data: Array<Any>?) {
        viewModelScope.launch {
            val serializedData =  data?.get(0) as JSONObject

            val latitude : Double; val longitude : Double


            try{
                latitude = serializedData.getDouble("latitude")
                longitude = serializedData.getDouble("longitude")

                _coordinatesState.update {
                    val coordinates : Coordinates = it?.copy(
                        latitude = latitude,
                        longitude = longitude,
                        date = formatter.format(Date())

                    )
                        ?: Coordinates(latitude = latitude,
                            longitude = longitude,
                            date = formatter.format(Date()))

                    coordinates

                }

            }catch (e : Exception){
                Log.e("Error serializing JSON", e.toString())

            }

        }
    }

    fun reconnectSocket() {
        if((ioSocket == null || (ioSocket?.connected() == false)) && !connecting){
            connectSocket()
        }
    }


    fun disconnectSocket() {
        ioSocket?.close()
        ioSocket?.disconnect()
        ioSocket = null

    }


}
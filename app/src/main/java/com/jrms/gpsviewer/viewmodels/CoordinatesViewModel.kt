package com.jrms.gpsviewer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.data.Coordinates
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


class CoordinatesViewModel() : ViewModel(){



    var coordinates : Coordinates? = null

    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    private val _coordinatesState = MutableStateFlow(coordinates)

    val coordinatesState  = _coordinatesState.asStateFlow()

    var deviceId : String? = null



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

                updateCoordinates(latitude, longitude)

            }catch (e : Exception){
                Log.e("Error serializing JSON", e.toString())

            }

        }
    }

    fun updateCoordinates( latitude : Double, longitude : Double, lastUpdate : String? = null){

        val date = lastUpdate ?: formatter.format(Date())

        _coordinatesState.update {
            this.coordinates = it?.copy(
                latitude = latitude,
                longitude = longitude,
                date = date

            )
                ?: Coordinates(latitude = latitude,
                    longitude = longitude,
                    date = date)

            coordinates

        }
    }

    fun startSocket() {
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
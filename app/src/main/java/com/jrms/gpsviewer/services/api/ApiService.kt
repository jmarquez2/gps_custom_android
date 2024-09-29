package com.jrms.gpsviewer.services.api

import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.models.Device
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val deviceAPI = retrofit.create(DeviceAPI::class.java)

    suspend fun getDevices(userId : String) : Response<Array<Device>>{
        return deviceAPI.getDeviceList(userId)
    }

}
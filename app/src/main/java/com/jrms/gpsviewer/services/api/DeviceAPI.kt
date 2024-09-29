package com.jrms.gpsviewer.services.api

import com.jrms.gpsviewer.models.Device
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DeviceAPI {
    @GET("/device/{userId}")
    suspend fun getDeviceList(@Path(value = "userId", encoded = true) userId : String) : Response<Array<Device>>
}
package com.jrms.gpsviewer.data

data class Coordinates(var latitude : Double, var longitude : Double, val date : String){

    fun getLatitudeText() : String{
        return latitude.toString()
    }

    fun getLongitudeText() : String{
        return longitude.toString()
    }
}

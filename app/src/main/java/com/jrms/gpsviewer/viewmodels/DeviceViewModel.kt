package com.jrms.gpsviewer.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.models.Device
import com.jrms.gpsviewer.services.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceViewModel(private val api : ApiService) : ViewModel() {

    private val _devices = MutableStateFlow<Array<Device>>(arrayOf())

    private val _error = MutableStateFlow(false);

    val devices = _devices.asStateFlow()
    val error = _error.asStateFlow()

    private val loadDevicesDispatcher = Dispatchers.IO



    fun loadDevices(){

        viewModelScope.launch {
            withContext(loadDevicesDispatcher) {
                try {
                    val res = api.getDevices(BuildConfig.ID)
                    if (res.body() != null) {
                        _devices.update {
                            res.body() as Array<Device>
                        }
                        _error.update {
                            false
                        }
                    }
                }catch (e : Exception){
                    _error.update {
                        true
                    }
                }
            }

        }



    }


}
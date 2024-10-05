package com.jrms.gpsviewer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.jrms.gpsviewer.BuildConfig
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.data.selectedDevice
import com.jrms.gpsviewer.dataStore
import com.jrms.gpsviewer.interfaces.OnSocketAction
import com.jrms.gpsviewer.models.Device
import com.jrms.gpsviewer.services.api.ApiService
import com.jrms.gpsviewer.ui.AppTheme
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class DevicesFragment : Fragment() {


    private val apiService : ApiService by inject()
    private val devices = mutableStateListOf<Device>()
    private val viewModel : CoordinatesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val res = apiService.getDevices(BuildConfig.ID)
                if(res.body() != null){
                    devices.clear()
                    devices.addAll(res.body() as Array<Device>)
                }

            }
        }


        return ComposeView(requireContext()).apply {
            setContent {
                DeviceList(devices)
            }
        }
    }






    @Composable
    fun DeviceList(devices : MutableList<Device>){

        AppTheme {
            Surface (modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background){
                Column(modifier = Modifier.padding(all = 10.dp)) {
                    Text(fontWeight = FontWeight.Bold, text = stringResource(R.string.deviceList))

                    Row (modifier = Modifier.padding(top = 20.dp)) {
                        LazyColumn {



                            items(devices) { d ->
                                Card (modifier = Modifier.fillMaxSize().
                                then(Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource()},
                                    indication = rememberRipple(bounded = false),
                                    onClick = { selectDevice(d)}))
                                ) {
                                    Row {
                                        Text(text = "${stringResource(R.string.id)}: ", modifier = Modifier.padding(10.dp, 10.dp, 5.dp, 0.dp), fontWeight = FontWeight.Bold)
                                        Text(d.id, modifier = Modifier.padding(top = 10.dp))
                                    }

                                    Row{
                                        Text(text = "${stringResource(R.string.description)}: ", modifier = Modifier.padding(10.dp, 10.dp, 5.dp, 10.dp), fontWeight = FontWeight.Bold)
                                        Text(d.description ?: "N/A", modifier = Modifier.padding(top = 10.dp))
                                    }

                                }


                            }
                        }
                    }

                }



            }
        }


    }

    private fun selectDevice(d : Device){
        viewLifecycleOwner.lifecycleScope.launch {
            activity?.baseContext?.dataStore?.edit {
                it[selectedDevice] = d.id
            }


            Toast.makeText(activity,
                "${getString(R.string.selectedDeviceConfirm)}: ${d.description ?: d.id}",
                Toast.LENGTH_SHORT).show()

        }

    }

    @Preview
    @Composable
    fun ShowDeviceList(){

        devices.add(Device("123123", "1231243", "test"))
        DeviceList(devices)
    }


}
package com.jrms.gpsviewer.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.data.selectedDevice
import com.jrms.gpsviewer.dataStore
import com.jrms.gpsviewer.models.Device
import com.jrms.gpsviewer.ui.AppTheme
import com.jrms.gpsviewer.viewmodels.DeviceViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DevicesFragment : Fragment() {


    private val viewModel : DeviceViewModel by viewModel()

    private var devices = mutableStateListOf<Device>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.loadDevices()


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.devices.collect {
                    devices.clear()
                    devices.addAll(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.error.collect {
                    if (activity != null && it) {
                        Toast.makeText(activity, R.string.errorGetDevices, Toast.LENGTH_SHORT)
                            .show()
                    }
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
                                Card (modifier = Modifier.fillMaxSize().padding(0.dp, 5.dp)
                                then(Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource()},
                                    indication = rememberRipple(bounded = true),
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
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activity?.baseContext?.dataStore?.edit {
                    it[selectedDevice] = d.id
                }


                if(activity != null) {
                    Toast.makeText(
                        activity,
                        "${getString(R.string.selectedDeviceConfirm)}: ${d.description ?: d.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    @Preview
    @Composable
    fun ShowDeviceList(){

        val list = remember {
            mutableStateListOf<Device>()
        }



        list.add(Device("123123", "1231243", "test"))
        list.add(Device("123123", "1231243", "test2"))
        DeviceList(list)
    }


}
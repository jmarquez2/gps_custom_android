package com.jrms.gpsviewer.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.data.followDeviceMarkerPreference
import com.jrms.gpsviewer.data.latitudePreference
import com.jrms.gpsviewer.data.longitudePreference
import com.jrms.gpsviewer.dataStore
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MapsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?


        var starting = savedInstanceState == null;

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                activity?.baseContext?.dataStore?.data?.collect {
                    val latitude = it[latitudePreference] ?: 0.0
                    val longitude = it[longitudePreference] ?: 0.0
                    val followMarker = it[followDeviceMarkerPreference] ?: true

                    mapFragment?.getMapAsync { googleMap ->
                        googleMap.clear()
                        val currentLocation = LatLng(latitude, longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(currentLocation).title(getString(R.string.deviceLastLocation))
                        )

                        if(followMarker || starting){
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))

                            starting = false
                        }

                    }

                }
            }
        }

    }
}
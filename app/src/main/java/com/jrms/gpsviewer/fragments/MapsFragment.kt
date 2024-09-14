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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import kotlinx.coroutines.launch

import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {

    private val viewModel : CoordinatesViewModel by viewModel()


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.coordinatesState.collect{
                    val latitude = it.latitude
                    val longitude = it.longitude

                    mapFragment?.getMapAsync { googleMap ->
                        googleMap.clear()
                        val currentLocation = LatLng(latitude, longitude)
                        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Current location"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                }

                }
            }
        }

        mapFragment?.getMapAsync(callback)
    }
}
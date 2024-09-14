package com.jrms.gpsviewer.fragments

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jrms.gpsviewer.R

import com.jrms.gpsviewer.databinding.FragmentCoordinatesBinding
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel

import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CoordinatesFragment : Fragment() {

    private val viewModel : CoordinatesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding =  DataBindingUtil.inflate<FragmentCoordinatesBinding>(inflater,
            R.layout.fragment_coordinates, container, false)



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.coordinatesState.collect{
                    binding.coordinates = it
                }
            }
        }

        childFragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.fragment_container_view, MapsFragment::class.java, null).commit()

        return binding.root

    }


    override fun onResume() {
        super.onResume()
        viewModel.reconnectSocket()
    }


}
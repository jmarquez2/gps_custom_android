package com.jrms.gpsviewer.fragments

import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jrms.gpsviewer.R
import com.jrms.gpsviewer.data.followDeviceMarkerPreference
import com.jrms.gpsviewer.dataStore

import com.jrms.gpsviewer.databinding.FragmentCoordinatesBinding
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.activityViewModel



/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CoordinatesFragment : Fragment() {

    private val viewModel : CoordinatesViewModel by activityViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding =  DataBindingUtil.inflate<FragmentCoordinatesBinding>(inflater,
            R.layout.fragment_coordinates, container, false)



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                activity?.baseContext?.dataStore?.data?.collect{
                    binding.followDeviceCheckBox.isChecked = it[followDeviceMarkerPreference] ?: true
                }
            }
        }




        childFragmentManager.beginTransaction().setReorderingAllowed(true)
            .add(R.id.fragment_container_view, MapsFragment::class.java, null).commit()


        binding.followDeviceCheckBox.setOnClickListener { c ->
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    activity?.baseContext?.dataStore?.edit {
                        it[followDeviceMarkerPreference] = (c as CheckBox).isChecked
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                this@CoordinatesFragment.viewModel.coordinatesState.collect{
                    if(it != null){
                        binding.coordinates = it
                    }
                }
            }
        }

        return binding.root

    }






}
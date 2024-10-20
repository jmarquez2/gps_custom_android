package com.jrms.gpsviewer

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jrms.gpsviewer.data.selectedDevice
import com.jrms.gpsviewer.databinding.MainActivityBinding
import com.jrms.gpsviewer.interfaces.OnSocketAction

import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel


import kotlinx.coroutines.launch

import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), OnSocketAction {

    private lateinit var binding: MainActivityBinding

    private val viewModel: CoordinatesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            startSocket()
        }

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_location, R.id.navigation_devices,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                baseContext?.dataStore?.data?.collect {
                    viewModel.setCoordinatesAndConnect(it[selectedDevice])
                }
            }

        }


    }

    override fun startSocket() {
        viewModel.startSocket()

    }

    override fun stopSocket() {
        viewModel.disconnectSocket()
    }

    override fun restartSocket() {
        viewModel.disconnectSocket()
        viewModel.startSocket()
    }

    override fun onRestart() {
        super.onRestart()
        startSocket()
    }

    override fun onStop() {
        viewModel.disconnectSocket(true)
        super.onStop()

    }


}
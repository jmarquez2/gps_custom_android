package com.jrms.gpsviewer

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jrms.gpsviewer.data.lastUpdatePreference
import com.jrms.gpsviewer.data.latitudePreference
import com.jrms.gpsviewer.data.longitudePreference
import com.jrms.gpsviewer.databinding.MainActivityBinding
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel



class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private val viewModel : CoordinatesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        if(savedInstanceState !== null){
            viewModel.reconnectSocket()
        }



        this.lifecycleScope.launch {
            withContext(Dispatchers.IO){
                this@MainActivity.viewModel.coordinatesState.collect{ data ->
                    this@MainActivity.baseContext.dataStore.edit {
                        if(data != null) {
                            it[lastUpdatePreference] = data.date
                            it[latitudePreference] = data.latitude
                            it[longitudePreference] = data.longitude
                        }
                    }
                }
            }
        }



    }

    override fun onRestart() {
        super.onRestart()
        viewModel.reconnectSocket()
    }


}
package com.jrms.gpsviewer

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jrms.gpsviewer.database.DatabaseService
import com.jrms.gpsviewer.services.api.ApiService
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import com.jrms.gpsviewer.viewmodels.DeviceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configs")

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@MainApplication)

            modules(
                module {
                    single { DatabaseService(get())}
                    viewModel { CoordinatesViewModel(get()) }
                    viewModel { DeviceViewModel(get()) }
                    single { ApiService() }


                }
            )

        }
    }
}
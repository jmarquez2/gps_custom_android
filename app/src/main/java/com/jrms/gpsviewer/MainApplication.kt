package com.jrms.gpsviewer

import android.app.Application
import com.jrms.gpsviewer.viewmodels.CoordinatesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@MainApplication)

            modules(
                module {
                    viewModel { CoordinatesViewModel(get()) }
                }
            )

        }
    }
}
package com.jrms.gpsviewer.data

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val lastUpdatePreference = stringPreferencesKey("lastUpdate")
val latitudePreference = doublePreferencesKey("latitude")
val longitudePreference = doublePreferencesKey("longitude")
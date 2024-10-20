package com.jrms.gpsviewer.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


val followDeviceMarkerPreference = booleanPreferencesKey("deviceMarker")
val selectedDevice = stringPreferencesKey("selectedDevice")
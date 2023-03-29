package com.github.cesar1287.mapsandroid.model

import com.google.android.gms.common.ConnectionResult

data class GoogleApiConnectionStatus(
    val success: Boolean,
    val connectionResult: ConnectionResult? = null
)


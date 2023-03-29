package com.github.cesar1287.mapsandroid.maps

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.cesar1287.mapsandroid.model.GoogleApiConnectionStatus
import com.github.cesar1287.mapsandroid.model.LocationError
import com.github.cesar1287.mapsandroid.model.MapState
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapsViewModel(
    application: Application
): AndroidViewModel(application) {

    private fun getContext() = getApplication<Application>()

    private val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            getContext()
        )
    }

    private val currentLocationError = MutableLiveData<LocationError>()

    private var googleApiClient: GoogleApiClient? = null
    private val connectionStatus = MutableLiveData<GoogleApiConnectionStatus>()

    private val mapState = MutableLiveData<MapState>().apply {
        value = MapState()
    }

    fun getConnectionStatus(): LiveData<GoogleApiConnectionStatus> {
        return connectionStatus
    }

    fun getCurrentLocationError(): LiveData<LocationError> {
        return currentLocationError
    }

    fun getMapState(): LiveData<MapState> {
        return mapState
    }

    fun getUserLocation() {
        viewModelScope.launch {
            currentLocationError.value = try {
                val success = withContext(Dispatchers.Default) { loadLastLocation() }
                if (success) {
                    null
                } else {
                    LocationError.ErrorLocationUnavailable
                }
            } catch (e: Exception) {
                LocationError.ErrorLocationUnavailable
            }

        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun loadLastLocation(): Boolean = suspendCoroutine { continuation ->
        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(
                    location.latitude,
                    location.longitude
                )
                mapState.value = mapState.value?.copy(origin = latLng)
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }.addOnFailureListener {
            continuation.resume(false)
        }.addOnCanceledListener { continuation.resume(false) }
    }


    fun connectGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(getContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(args: Bundle?) {
                        connectionStatus.value = GoogleApiConnectionStatus(true)
                    }

                    override fun onConnectionSuspended(i: Int) {
                        connectionStatus.value = GoogleApiConnectionStatus(false)
                        googleApiClient?.connect()
                    }
                })
                .addOnConnectionFailedListener { connectionResult ->
                    connectionStatus.value = GoogleApiConnectionStatus(false, connectionResult)
                }.build()
        }
        googleApiClient?.connect()
    }

    fun disconnectGoogleApiClient() {
        connectionStatus.value = GoogleApiConnectionStatus(false)
        if (googleApiClient != null && googleApiClient?.isConnected == true) {
            googleApiClient?.disconnect()
        }
    }
}
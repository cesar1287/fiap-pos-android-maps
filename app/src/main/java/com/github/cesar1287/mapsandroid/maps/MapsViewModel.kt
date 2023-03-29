package com.github.cesar1287.mapsandroid.maps

import android.annotation.SuppressLint
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.cesar1287.mapsandroid.model.GoogleApiConnectionStatus
import com.github.cesar1287.mapsandroid.model.LocationError
import com.github.cesar1287.mapsandroid.model.MapState
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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

    private val addresses = MutableLiveData<List<Address>?>()
    private val loading = MutableLiveData<Boolean>()

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

    fun searchAddress(search: String) {
        viewModelScope.launch {
            loading.value = true
            val geoCoder = Geocoder(
                getContext(),
                Locale.getDefault()
            )
            addresses.value = withContext(Dispatchers.IO) {
                geoCoder.getFromLocationName(search, 10)
            }
            loading.value = false
        }
    }


    fun getAddresses(): LiveData<List<Address>?> {
        return addresses
    }

    fun isLoading(): LiveData<Boolean> {
        return loading
    }

    fun clearSearchAddressResult() {
        addresses.value = null
    }

    fun setDestination(latLng: LatLng) {
        addresses.value = null
        mapState.value = mapState.value?.copy(destination = latLng)
    }


    private suspend fun checkGpsStatus(): Boolean = suspendCoroutine { continuation ->
        val request =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val locationSettingsRequest = LocationSettingsRequest.Builder().setAlwaysShow(true)
            .addLocationRequest(request)
        SettingsClient(getContext()).checkLocationSettings(
            locationSettingsRequest.build()
        ).addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                continuation.resume(true)
            } catch (exception: ApiException) {
                continuation.resumeWithException(exception)
            }
        }.addOnCanceledListener { continuation.resume(false) }
    }


    fun getUserLocation() {
        viewModelScope.launch {
            currentLocationError.value = try {
                checkGpsStatus()
                val success = withTimeout(20000) { loadLastLocation() }
                if (success) {null} else {
                    LocationError.ErrorLocationUnavailable
                }
            } catch (timeout: TimeoutCancellationException) {
                LocationError.ErrorLocationUnavailable
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        LocationError.GpsDisabled(exception as ResolvableApiException)
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        LocationError.GpsSettingUnavailable
                    else -> LocationError.ErrorLocationUnavailable
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun loadLastLocation(): Boolean = suspendCoroutine { continuation ->
        fun updateOriginByLocation(location: Location) {
            val latLng = LatLng(
                location.latitude,
                location.longitude
            )
            mapState.value = mapState.value?.copy(origin = latLng)
            continuation.resume(true)
        }

        fun waitForLocation() {
            val locationRequest =
                LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5 * 1000)
                    .setFastestInterval(1 * 1000)
            locationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    locationClient.removeLocationUpdates(this)
                    val location = result.lastLocation
                    if (location != null) {
                        updateOriginByLocation(location)
                    } else {
                        continuation.resume(false)
                    }
                }
            }, Looper.getMainLooper())
        }

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                waitForLocation()
            } else {
                updateOriginByLocation(location)
            }
        }.addOnFailureListener { waitForLocation() }
            .addOnCanceledListener { continuation.resume(false) }
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
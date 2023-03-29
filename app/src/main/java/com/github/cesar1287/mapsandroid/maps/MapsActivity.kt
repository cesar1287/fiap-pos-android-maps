package com.github.cesar1287.mapsandroid.maps

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.github.cesar1287.mapsandroid.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.github.cesar1287.mapsandroid.databinding.ActivityMapsBinding
import com.github.cesar1287.mapsandroid.model.LocationError
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val binding: ActivityMapsBinding by lazy {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this)[MapsViewModel::class.java]
    }

    private val fragment: MyMapFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.map) as MyMapFragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        fragment.getMapAsync {
            initUi()
            viewModel.connectGoogleApiClient()
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.disconnectGoogleApiClient()
    }

    private fun initUi() {
        viewModel.getConnectionStatus().observe(this) { status ->
            if (status != null) {
                if (status.success) {
                    loadLastLocation()
                } else {
                    status.connectionResult?.let {
                        handleConnectionError(it)
                    }
                }
            }
        }

        viewModel.getCurrentLocationError().observe(
            this
        ) { error -> handleLocationError(error) }
    }

    private fun handleConnectionError(result: ConnectionResult) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_ERROR_PLAY_SERVICES)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            showPlayServicesErrorMessage(result.errorCode)
        }
    }

    private fun handleLocationError(error: LocationError?) {
        if (error != null) {
            when (error) {
                is LocationError.ErrorLocationUnavailable -> showError("Não foi possivel obter a localização atual")
                is LocationError.GpsDisabled -> showError("Não foi possivel obter a localização atual")
                is LocationError.GpsSettingUnavailable -> showError("Não foi possivel obter a localização atual")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == REQUEST_PERMISSIONS && permissions.isNotEmpty()) {
            if (permissions.firstOrNull() == android.Manifest.permission.ACCESS_FINE_LOCATION
                && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                loadLastLocation()
            } else {
                showError("Você deve aceitar as permissões para executar a aplicação")
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ERROR_PLAY_SERVICES && resultCode == Activity.RESULT_OK) {
            viewModel.connectGoogleApiClient()
        }
    }

    private fun loadLastLocation() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS
            )
            return
        }
        viewModel.getUserLocation()
    }

    private fun hasPermission(): Boolean {
        val granted = PackageManager.PERMISSION_GRANTED
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == granted
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showPlayServicesErrorMessage(errorCode: Int) {
        GoogleApiAvailability.getInstance()
            .getErrorDialog(this, errorCode, REQUEST_ERROR_PLAY_SERVICES)?.show()
    }

    companion object {
        private const val REQUEST_ERROR_PLAY_SERVICES = 1
        private const val REQUEST_PERMISSIONS = 2
    }

/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

//        val fiap = LatLng(-23.59219, -46.685316)
//
//        val circleOptions = CircleOptions()
//        circleOptions.center(fiap)
//        circleOptions.radius(200.0)
//        circleOptions.fillColor(Color.argb(128, 0, 51, 102))
//        circleOptions.strokeWidth(10f)
//        circleOptions.strokeColor(Color.argb(128, 0, 51, 102))
//
//        mMap.addMarker(MarkerOptions().position(fiap).title("Fiap"))
//        mMap.addCircle(circleOptions)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiap, 16f))

//        val fiapVilaOlimpia = LatLng(-23.595219, -46.685316)
//        val shoppingVilaOlimpia = LatLng(-23.595343, -46.686796)
//
//        val polylineOptions = PolylineOptions()
//        polylineOptions.add(fiapVilaOlimpia)
//        polylineOptions.add(shoppingVilaOlimpia)
//        polylineOptions.color(Color.GREEN)
//        polylineOptions.width(15f)
//
//        mMap.addPolyline(polylineOptions)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiapVilaOlimpia, 16f))

//        val fiapVilaOlimpia = LatLng(-23.595219, -46.685316)
//        val shoppingVilaOlimpia = LatLng(-23.595343, -46.686796)
//
//        val polylineOptions = PolylineOptions()
//        polylineOptions.add(fiapVilaOlimpia)
//        polylineOptions.add(shoppingVilaOlimpia)
//        polylineOptions.color(Color.GREEN)
//        polylineOptions.width(15f)
//
//        mMap.addPolyline(polylineOptions)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiapVilaOlimpia, 16f))

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-3.7899632,-38.5889775)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(sydney)
//                .title("Marker in Fortaleza")
//                .snippet("This marker represents the Fortaleza city")
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val geocoder = Geocoder(this, Locale.getDefault())
        val address = "Rua Fidêncio Ramos, 302"
        val addressGeocoding = geocoder.getFromLocationName(address, 1)

        val addressByLocation = geocoder.getFromLocation(-23.595219, -46.685316, 1)
    }
}
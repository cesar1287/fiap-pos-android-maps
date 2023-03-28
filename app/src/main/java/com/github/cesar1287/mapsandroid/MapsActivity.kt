package com.github.cesar1287.mapsandroid

import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.github.cesar1287.mapsandroid.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
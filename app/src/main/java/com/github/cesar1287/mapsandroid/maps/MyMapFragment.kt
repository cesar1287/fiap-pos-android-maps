package com.github.cesar1287.mapsandroid.maps

import androidx.lifecycle.ViewModelProvider
import com.github.cesar1287.mapsandroid.model.MapState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MyMapFragment : SupportMapFragment() {

    private var googleMap: GoogleMap? = null

    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(requireActivity())[MapsViewModel::class.java]
    }

    override fun getMapAsync(callback: OnMapReadyCallback) {
        super.getMapAsync {
            googleMap = it
            setupMap()
            googleMap?.let { googleMap ->
                callback.onMapReady(googleMap)
            }
        }
    }

    private fun setupMap() {
        googleMap?.run {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isZoomControlsEnabled = true
        }

        viewModel.getMapState().observe(
            this
        ) { mapState ->
            mapState?.let {
                updateMap(mapState)
            }
        }
    }

    private fun updateMap(mapState: MapState) {
        googleMap?.run {
            clear()
            val area = LatLngBounds.Builder()
            val origin = mapState.origin
            if (origin != null) {
                addMarker(
                    MarkerOptions()
                        .position(origin).title("Buscando o endereco")
                )
                area.include(origin)
            }

            val destination = mapState.destination
            if (destination != null) {
                addMarker(MarkerOptions().position(destination)
                    .title("Destino"))
                area.include(destination)
            }
            if (origin != null) {
                if (destination != null) {
                    animateCamera(CameraUpdateFactory.newLatLngBounds(area.build(), 50))
                } else {
                    animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 17f))
                }
            }
        }
    }
}

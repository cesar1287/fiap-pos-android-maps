package com.github.cesar1287.mapsandroid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.cesar1287.mapsandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btIntentMap.setOnClickListener {
            val navigation = "google.streetview:panoid=BQMXpbyPOUavEzjU1f4wbQ"

            val navigationUri = Uri.parse(navigation)
            val intent = Intent(Intent.ACTION_VIEW, navigationUri)

            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)

//            val address = "Avenida Bezerra de Menezes, 1800, Fortaleza, Ceará, Brasil"
//            val location = Uri.encode( address )
//            val navigation = "google.navigation:q=$location"
//
//            val navigationUri = Uri.parse( navigation )
//            val intent = Intent( Intent.ACTION_VIEW, navigationUri )
//
//            intent.setPackage( "com.google.android.apps.maps" )
//            startActivity( intent )

//            val query = "baladas"
//            val latitudeLongitude = "-72.5565804,-54.662113"
//            val geo = "geo:$latitudeLongitude?q=$query"
//
//            val geoUri = Uri.parse( geo )
//            val intent = Intent( Intent.ACTION_VIEW, geoUri )
//
//            intent.setPackage( "com.google.android.apps.maps" )
//            startActivity( intent )

//            val query = "parques"
//            val zoom = 20
//            val geo = "geo:0,0?q=$query&z=$zoom"
//
//            val geoUri = Uri.parse( geo )
//            val intent = Intent( Intent.ACTION_VIEW, geoUri )
//
//            intent.setPackage( "com.google.android.apps.maps" )
//            startActivity( intent )

//            val latitudeLongitude = "-23.5565804,-46.662113"
//
//            val zoom = 15
//            val geo = "geo:$latitudeLongitude?z=$zoom"
//
//            val geoUri = Uri.parse( geo )
//            val intent = Intent( Intent.ACTION_VIEW, geoUri )
//
//            intent.setPackage( "com.google.android.apps.maps" )

            if( intent.resolveActivity( packageManager ) != null ) {
                startActivity( intent )
            }
            else{
                Toast.makeText(this, "Nenhum mapa instalado", Toast.LENGTH_LONG).show()
            }


        }

        binding.btNativeMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

}
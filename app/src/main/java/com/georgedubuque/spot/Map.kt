package com.georgedubuque.spot

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import com.georgedubuque.spot.R.layout.activity_add_spot
import com.georgedubuque.spot.R.layout.activity_spot_map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class Map : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var lastLocation : Location
    private lateinit var mDrawerLayout: DrawerLayout
    private val ADD_SPOT_INTENT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }
    }

    private fun setUpMap(){

        if(ActivityCompat.checkSelfPermission(
                        this,android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
                                return}

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){

                lastLocation = location
                val currentLatLng = LatLng(location.latitude,location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,17f))
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setOnMapLongClickListener { latLng ->

            val intent = Intent(this, AddSpot::class.java)
            intent.putExtra("lat",latLng.latitude)
            intent.putExtra("lng",latLng.longitude)
            startActivityForResult(intent,ADD_SPOT_INTENT)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ADD_SPOT_INTENT){

            if(resultCode == Activity.RESULT_OK){
                print("we have returned from activity spot")
                val spot : Spot = data?.getSerializableExtra("spot") as Spot
                placeMarker(spot)
            }
        }
    }

    fun placeMarker(spot : Spot){

        val latLng = LatLng(spot.lat,spot.lng)

        mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(spot.name)
                .snippet(spot.type))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1
    }

}

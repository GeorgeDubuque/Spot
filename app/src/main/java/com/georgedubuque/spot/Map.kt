package com.georgedubuque.spot

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.georgedubuque.spot.R.layout.activity_spot_list
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_spot_map.*

/*
purpose:    This is the main activity and reacts to long clicks on the map to add markers. You can
            click on spot info to edit/delete the spot. Holds map fragment and tracks user location.
            handles user permissions for location.
 */

class Map : AppCompatActivity(), OnMapReadyCallback {

    var db = MyDBHandler(this,null,null,2)
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var lastLocation : Location
    private lateinit var mDrawerLayout: DrawerLayout
    private val ADD_SPOT_INTENT = 1
    private val EDIT_SPOT_INTENT = 2
    private val DELETE = 4

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

            if(menuItem.title == "Spots"){

                mDrawerLayout.closeDrawers()
                val intent = Intent(this,SpotList::class.java)
                startActivity(intent)
            }

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        button_add_spot.setOnClickListener { addSpot() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.help_button) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Help:")
            builder.setMessage("Long click on a point on the map to set a marker and spot info. " +
                    "Click a marker to see basic info about the spot, click the info to get a more" +
                    "detailed description of the spot. Eventually you will be able to click the ADD" +
                    " button at the bottom to set a marker at your exact location." +
                    "Swipe from left to right to open the menu.")
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addSpot(){

        val intent = Intent(this, AddSpot::class.java)
        val lat = lastLocation.latitude
        val lon = lastLocation.longitude
        intent.putExtra("lat",lat)
        intent.putExtra("lng",lon)
        startActivityForResult(intent,ADD_SPOT_INTENT)
    }

    private fun loadSpots(){

        mMap.clear()
        var spots = db.getSpots()
        var latLng : LatLng

        for(spot in spots){

            latLng = LatLng(spot.lat,spot.lng)
            val marker = MarkerOptions().position(latLng)

            marker.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map",50,80)))
            mMap.addMarker(marker.position(latLng).title(spot.name).snippet(spot.type))
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

        mMap.setOnInfoWindowClickListener(OnInfoWindowClickListener { marker ->
            marker.remove()
            val spot = db.findSpot(marker.title)
            val latLng = marker.position
            val intent = Intent(this,AddSpot::class.java)
            intent.putExtra("lat",latLng.latitude)
            intent.putExtra("lng",latLng.longitude)
            intent.putExtra("spot",spot)
            startActivityForResult(intent,EDIT_SPOT_INTENT)
        })

        loadSpots()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
        // Add a marker in Sydney and move the camera
        mMap.uiSettings.isZoomControlsEnabled = true

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
                val spot : Spot = data?.getSerializableExtra("spot") as Spot
                Log.d("adding spot to database",spot.name)
                db.addSpot(spot)
                placeMarker(spot)
            }

        }else if(requestCode == EDIT_SPOT_INTENT){

            if(resultCode == Activity.RESULT_OK){
                val spot : Spot = data?.getSerializableExtra("spot") as Spot
                val oldName : String = data?.getStringExtra("oldName")
                db.deleteSpot(oldName)
                db.addSpot(spot)
                placeMarker(spot)
            }else if (resultCode == DELETE){
                val spot : Spot = data?.getSerializableExtra("spot") as Spot

                if(db.findSpot(spot.name) != null){

                    db.deleteSpot(spot.name)
                }
            }
        }
    }

    fun placeMarker(spot : Spot){

        val latLng = LatLng(spot.lat,spot.lng)
        val marker = MarkerOptions().position(latLng)

        marker.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map",50,80)))

        mMap.addMarker(
                marker.position(latLng)
                .title(spot.name)
                .snippet(spot.type))
    }

    fun resizeMapIcons(iconName : String, width : Int,height : Int):Bitmap{
        val imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1
    }

}

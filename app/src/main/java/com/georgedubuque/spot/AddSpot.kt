package com.georgedubuque.spot

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.io.Serializable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_spot.*

class AddSpot : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)

        val adapter = ArrayAdapter.createFromResource(this,R.array.spot_types,
                android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spot_type.adapter = adapter
        button_edit.setOnClickListener{ editSpot() }
    }

    fun editSpot(){
        val lat = intent.getDoubleExtra("lat",0.0)
        val lng = intent.getDoubleExtra("lng",0.0)
        val latLng = LatLng(lat,lng)
        val spot = Spot(spot_name.text.toString(),spot_type.selectedItem.toString(),
                spot_rating.numStars,spot_img.drawable.toString(),lat,lng)

        val intent = Intent()
        intent.putExtra("spot",spot)
        setResult(RESULT_OK,intent)
        finish()
    }


}

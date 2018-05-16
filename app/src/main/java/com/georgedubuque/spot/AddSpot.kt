package com.georgedubuque.spot

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.io.Serializable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_spot.*

class AddSpot : AppCompatActivity(){

    var oldName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)

        val adapter = ArrayAdapter.createFromResource(this,R.array.spot_types,
                android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spot_type.adapter = adapter
        button_edit.setOnClickListener{ editSpot() }
        button_delete.setOnClickListener { deleteSpot() }

        if(intent.getSerializableExtra("spot") != null){
            val spot = intent.getSerializableExtra("spot") as Spot
            spot_name.setText(spot.name)
            spot_rating.rating = spot.rating.toFloat()
            Log.d("numStars: ", spot.rating.toString())
            spot_type.setSelection(spot.typeNum)
            oldName = spot.name
        }
    }

    fun deleteSpot(){

        val lat = intent.getDoubleExtra("lat",0.0)
        val lng = intent.getDoubleExtra("lng",0.0)
        val latLng = LatLng(lat,lng)
        spot_type.selectedItemPosition
        val spot = Spot(spot_name.text.toString(),spot_type.selectedItem.toString(),
                spot_type.selectedItemPosition ,spot_rating.rating,spot_img.drawable.toString(),
                lat,lng)

        val intent = Intent()
        intent.putExtra("spot",spot)
        intent.putExtra("oldName",oldName)
        setResult(RESULT_CANCELED,intent)
        finish()
    }

    fun editSpot(){

        val lat = intent.getDoubleExtra("lat",0.0)
        val lng = intent.getDoubleExtra("lng",0.0)
        val latLng = LatLng(lat,lng)
        spot_type.selectedItemPosition
        val spot = Spot(spot_name.text.toString(),spot_type.selectedItem.toString(),
                spot_type.selectedItemPosition ,spot_rating.rating,spot_img.drawable.toString(),
                lat,lng)

        val intent = Intent()
        intent.putExtra("oldName",oldName)
        intent.putExtra("spot",spot)
        setResult(RESULT_OK,intent)
        finish()
    }


}

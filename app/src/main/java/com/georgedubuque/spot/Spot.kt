package com.georgedubuque.spot

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Spot : Serializable {


    var name = ""
    var type = ""
    var typeNum = 0
    var rating = 0f
    var img = ""
    var lat = 0.0
    var lng = 0.0
    var spotId: Int = 0

    fun setId(id : Int){

        this.spotId = id
    }


    constructor(name : String, type : String, typeNum : Int, rating: Float, img : String, lat: Double, lng : Double){

        this.name = name
        this.type = type
        this.typeNum = typeNum
        this.rating = rating
        this.img = img
        this.lat = lat
        this.lng = lng
    }

    constructor()
}
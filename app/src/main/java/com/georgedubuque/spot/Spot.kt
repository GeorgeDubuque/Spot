package com.georgedubuque.spot

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Spot : Serializable {

    var name = ""
    var type = ""
    var rating = 0
    var img = ""
    var lat = 0.0
    var lng = 0.0


    constructor(name : String, type : String, rating: Int, img : String, lat: Double, lng : Double){

        this.name = name
        this.type = type
        this.rating = rating
        this.img = img
        this.lat = lat
        this.lng = lng
    }
}
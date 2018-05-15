package com.georgedubuque.spot

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

public class Spot : Serializable {

    var name = ""
    var type = ""
    var rating = 0
    var img = ""
    var latLng = LatLng(0.0,0.0)

    constructor(name : String, type : String, rating: Int, img : String, latLng: LatLng){

        this.name = name
        this.type = type
        this.rating = rating
        this.img = img
        this.latLng = latLng
    }
}
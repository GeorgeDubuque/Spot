package com.georgedubuque.spot

import android.app.Fragment
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_spot_list.*

class SpotList : AppCompatActivity(), SpotListItem.OnFragmentInteractionListener {

    private var db : MyDBHandler = MyDBHandler(this,null,null, 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("creating activity",this.packageName)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_list)
        displaySpots()
    }

    private fun displaySpots(){

        var spots = db.getSpots()
        var trans = fragmentManager.beginTransaction()

        Log.d("in function","displaySpots()")

        for(spot in spots){

            val frag : Fragment = SpotListItem.newInstance(spot)
            trans.add(spot_list.id,frag,"frag " + spot.spotId)
        }

        trans.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

package com.georgedubuque.spot

import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_spot_list.*

/*
purpose:    this activity is used to dynamically create fragments and add them to the linear layout
            this activity has access to the databse
 */

class SpotList : AppCompatActivity(), SpotListItem.OnFragmentInteractionListener {

    private var db : MyDBHandler = MyDBHandler(this,null,null, 2)
    private lateinit var mDrawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("creating activity",this.packageName)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_list)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true

            if(menuItem.title == "Map"){

                mDrawerLayout.closeDrawers()
                val intent = Intent(this,Map::class.java)
                startActivity(intent)
            }

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }


        loadSpots()
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
            builder.setMessage("This is a view of all the spots you have added." +
                    "This will eventually allow you to add and delete spots from the map. As well " +
                    "as click to view spot. " +
                    "Swipe from left to right to open the menu.")
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadSpots(){

        var spots : ArrayList<Spot> = db.getSpots()
        var spot : Spot
        var trans = fragmentManager.beginTransaction()

        Log.d("in function","loadSpots()")

        for(i in spots.indices){
            spot = spots[i]
            Log.d("creating fragment" ,spot.name + " at index " + i)
            val frag : Fragment = SpotListItem.newInstance(spot)
            trans.add(spot_list.id,frag,"frag " + spot.spotId)
        }

        trans.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

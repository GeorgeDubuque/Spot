package com.georgedubuque.spot

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.georgedubuque.spot.R.layout.fragment_spot_list_item
import kotlinx.android.synthetic.main.activity_add_spot.*
import kotlinx.android.synthetic.main.activity_add_spot.view.*
import kotlinx.android.synthetic.main.fragment_spot_list_item.*
import kotlinx.android.synthetic.main.fragment_spot_list_item.view.*

/*
purpose:    fragment that displays spot list item. displays spot name, type, image and rating.
 */

class SpotListItem : Fragment(){

    // TODO: Rename and change types of parameters
    private lateinit var spotName: String
    private lateinit var spotType: String
    private lateinit var spotRating: String
    private lateinit var spotImg: String


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("created fragment",this.tag)

        if (arguments != null) {
            val spot = arguments.getSerializable(ARG_SPOT) as Spot

            spotName = spot.name
            spotType = spot.type
            spotImg = spot.img
            spotRating = spot.rating.toString()
            //TODO: implement taking picture of spot and storing before manipulating fragment image
            //spotImg = spot.img
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_spot_list_item, container, false)
        view.spot_list_name.text = spotName
        view.spot_list_type.text = spotType
        if(spotImg.isNotEmpty()){

            view.spot_list_image.setImageDrawable(Drawable.createFromPath(spotImg))
        }
        view.spot_list_rating.rating = spotRating.toFloat()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("context",context.packageName)


        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener  {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val ARG_SPOT = "param1"

        fun newInstance(spot: Spot): SpotListItem {
            val fragment = SpotListItem()
            val args = Bundle()
            args.putSerializable(ARG_SPOT, spot)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

package com.georgedubuque.spot

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.georgedubuque.spot.R.layout.fragment_spot_list_item
import kotlinx.android.synthetic.main.activity_add_spot.*
import kotlinx.android.synthetic.main.fragment_spot_list_item.*
import kotlinx.android.synthetic.main.fragment_spot_list_item.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SpotListItem.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SpotListItem.newInstance] factory method to
 * create an instance of this fragment.
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
            spotRating = spot.rating.toString()
            //TODO: implement taking picture of spot and storing before manipulating fragment image
            //spotImg = spot.img
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_spot_list_item, container, false)
        view.spot_name.text = spotName
        view.spot_type.text = spotType
        view.spot_rating.rating = spotRating.toFloat()
        return view
    }

     //TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener  {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val ARG_SPOT = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SpotListItem.
         */

        // TODO: Rename and change types and number of parameters
        fun newInstance(spot: Spot): SpotListItem {
            val fragment = SpotListItem()
            val args = Bundle()
            args.putSerializable(ARG_SPOT, spot)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

package com.example.pointme

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!.applicationContext, apiKey)
        }

        val geocoder = Geocoder(activity!!, Locale.getDefault())

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val addresses: List<Address> =
                    geocoder.getFromLocationName(place.name, 1)

                navToPointer(addresses[0], place.name!!)
            }

            override fun onError(status: Status) {
                // todo improve error handling
            }
        })
    }

    fun navToPointer(address: Address, placeName: String) {
        val bundle = bundleOf(
            EXTRA_LAT to address.latitude,
            EXTRA_LNG to address.longitude,
            EXTRA_DEST to placeName)

        findNavController().navigate(R.id.action_location_to_arrow, bundle)
    }
}
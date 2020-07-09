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
import com.example.pointme.listeners.DestinationSelectionListener
import com.example.pointme.managers.DatabaseManager
import com.example.pointme.managers.NavigationStartManager
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

        var navigationStartManager = NavigationStartManager(
            DatabaseManager().getDatabase(activity!!.applicationContext).navigationStartRepository())

        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!.applicationContext, apiKey)
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(DestinationSelectionListener(findNavController(), navigationStartManager))
    }
}
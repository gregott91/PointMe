package com.example.pointme.logic

import android.content.Context
import android.view.View
import com.example.pointme.R
import com.example.pointme.platform.listeners.DestinationSelectionListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlacesProxy @Inject constructor(
    @ApplicationContext private val context: Context,
    private val destinationSelectionListener: DestinationSelectionListener
) {
    fun initialize(placeFragment: AutocompleteSupportFragment) {
        initializeApi()

        placeFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        placeFragment.setOnPlaceSelectedListener(destinationSelectionListener)
    }

    private fun initializeApi() {
        val apiKey = context.getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
    }

    fun openFragment(placeFragment: AutocompleteSupportFragment) {
        val root: View = placeFragment.view!!
        root.post {
            root.findViewById<View>(R.id.places_autocomplete_search_input).performClick()
        }
    }
}
package com.example.pointme.listeners

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.pointme.EXTRA_DEST
import com.example.pointme.EXTRA_LAT
import com.example.pointme.EXTRA_LNG
import com.example.pointme.R
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class DestinationSelectionListener(controller: NavController) : PlaceSelectionListener {
    private val mController: NavController = controller

    override fun onPlaceSelected(place: Place) {
        val bundle = bundleOf(
            EXTRA_LAT to place.latLng?.latitude,
            EXTRA_LNG to place.latLng?.longitude,
            EXTRA_DEST to place.name)

        mController.navigate(R.id.action_location_to_arrow, bundle)
    }

    override fun onError(p0: Status) {
        // todo add error handling plz
    }
}
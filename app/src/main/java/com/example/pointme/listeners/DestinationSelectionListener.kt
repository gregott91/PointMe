package com.example.pointme.listeners

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.pointme.EXTRA_DEST
import com.example.pointme.EXTRA_LAT
import com.example.pointme.EXTRA_LNG
import com.example.pointme.R
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class DestinationSelectionListener(activity: Activity, controller: NavController) : PlaceSelectionListener {
    private val mActivity: Activity = activity
    private val mController: NavController = controller

    override fun onPlaceSelected(place: Place) {
        val geocoder = Geocoder(mActivity, Locale.getDefault())

        val address: Address = geocoder.getFromLocationName(place.name, 1)[0]

        val bundle = bundleOf(
            EXTRA_LAT to address.latitude,
            EXTRA_LNG to address.longitude,
            EXTRA_DEST to place.name)

        mController.navigate(R.id.action_location_to_arrow, bundle)
    }

    override fun onError(p0: Status) {
        // todo add error handling plz
    }
}
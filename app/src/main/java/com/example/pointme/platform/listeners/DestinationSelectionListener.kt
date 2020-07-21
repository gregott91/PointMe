package com.example.pointme.platform.listeners

import androidx.navigation.NavController
import com.example.pointme.R
import com.example.pointme.logic.managers.NavigationRequestManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DestinationSelectionListener(controller: NavController, navigationStartManager: NavigationRequestManager) : PlaceSelectionListener {
    private val mController: NavController = controller
    private val mNavigationStartManager: NavigationRequestManager = navigationStartManager

    override fun onPlaceSelected(place: Place) {
        runBlocking {
            withContext(Dispatchers.IO) {
                mNavigationStartManager.updateActiveNavigation(place.name!!, place.latLng?.latitude!!, place.latLng?.longitude!!)
            }
        }

        mController.navigate(R.id.action_location_to_arrow)
    }

    override fun onError(p0: Status) {
        // todo add error handling plz
    }
}
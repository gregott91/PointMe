package com.example.pointme.platform.listeners

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.pointme.R
import com.example.pointme.logic.managers.NavigationRequestManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DestinationSelectionListener @Inject constructor(
    private val fragment: Fragment,
    private val navigationRequestManager: NavigationRequestManager
) : PlaceSelectionListener {

    override fun onPlaceSelected(place: Place) {
        runBlocking {
            withContext(Dispatchers.IO) {
                navigationRequestManager.updateActiveNavigation(place.name!!, place.latLng?.latitude!!, place.latLng?.longitude!!)
            }
        }

        var navController = fragment.findNavController()
        navController.navigate(R.id.action_location_to_arrow)
    }

    override fun onError(p0: Status) {
        // todo add error handling plz
    }
}
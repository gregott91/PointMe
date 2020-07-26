package com.example.pointme.platform.listeners

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pointme.DESTINATION_LOG_TAG
import com.example.pointme.R
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.models.MessageDuration
import com.example.pointme.platform.MessageDisplayer
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DestinationSelectionListener @Inject constructor(
    private val fragment: Fragment,
    private val navigationRequestManager: NavigationRequestManager,
    private val messageDisplayer: MessageDisplayer
) : PlaceSelectionListener {

    override fun onPlaceSelected(place: Place) {
        runBlocking {
            withContext(Dispatchers.IO) {
                navigationRequestManager.updateActiveNavigation(place.name!!, place.latLng?.latitude!!, place.latLng?.longitude!!)
            }
        }

        val navController = fragment.findNavController()
        navController.navigate(R.id.action_location_to_arrow)
    }

    override fun onError(p0: Status) {
        Log.e(DESTINATION_LOG_TAG, p0.statusMessage!!)
        messageDisplayer.displayMessage(fragment.resources.getString(R.string.place_error), MessageDuration.SHORT)
    }
}
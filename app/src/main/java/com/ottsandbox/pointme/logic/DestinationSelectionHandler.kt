package com.ottsandbox.pointme.logic

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.logic.managers.NavigationRequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DestinationSelectionHandler @Inject constructor(
    private val fragment: Fragment,
    private val navigationRequestManager: NavigationRequestManager
) {
    fun handlePlaceSelected(name: String, latitude: Double, longitude: Double) {
        runBlocking {
            withContext(Dispatchers.IO) {
                navigationRequestManager.updateActiveNavigation(name, latitude, longitude)
            }
        }

        val navController = fragment.findNavController()
        navController.navigate(R.id.action_location_to_arrow)
    }
}
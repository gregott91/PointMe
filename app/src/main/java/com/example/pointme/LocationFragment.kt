package com.example.pointme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointme.platform.listeners.DestinationSelectionListener
import com.example.pointme.managers.DatabaseManager
import com.example.pointme.managers.NavigationOperationManager
import com.example.pointme.managers.NavigationRequestManager
import com.example.pointme.platform.adapters.NavigationAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LocationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var database = DatabaseManager().getDatabase(activity!!.applicationContext)

        var operationManager = NavigationOperationManager(database.navigationOperationRepository())
        var requestManager = NavigationRequestManager(database.navigationStartRepository())

        initializePlaces()

        runBlocking {
            withContext(Dispatchers.IO) {
                viewManager = LinearLayoutManager(activity!!)
                viewAdapter = NavigationAdapter(operationManager.getLastSessions(DEFAULT_SESSION_LIMIT))

                recyclerView = activity!!.findViewById<RecyclerView>(R.id.previous_activities).apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
            }
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(DestinationSelectionListener(findNavController(), requestManager))
    }

    // todo extract this code
    private fun initializePlaces() {
        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!.applicationContext, apiKey)
        }
    }
}
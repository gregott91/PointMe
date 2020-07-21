package com.example.pointme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointme.data.databases.AppDatabase
import com.example.pointme.logic.CoroutineRunner
import com.example.pointme.platform.listeners.DestinationSelectionListener
import com.example.pointme.logic.managers.DatabaseManager
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.logic.settings.DistancePreferenceManager
import com.example.pointme.logic.settings.PreferenceProxy
import com.example.pointme.platform.adapters.NavigationAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LocationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var database: AppDatabase
    private var coroutineRunner: CoroutineRunner = CoroutineRunner()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = DatabaseManager().getDatabase(activity!!.applicationContext)

        val requestManager =
            NavigationRequestManager(
                database.navigationStartRepository(),
                database.coordinateEntityRepository())

        initializePlaces()

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(DestinationSelectionListener(findNavController(), requestManager))

        val operationManager =
            NavigationOperationManager(database.navigationOperationRepository())
        val preferenceManager = DistancePreferenceManager(PreferenceProxy())

        coroutineRunner.run {
            val sessions = operationManager.getLastSessions(DEFAULT_SESSION_LIMIT)

            viewManager = LinearLayoutManager(activity!!)
            viewAdapter = NavigationAdapter(sessions, activity!!, preferenceManager)

            recyclerView = activity!!.findViewById<RecyclerView>(R.id.previous_activities).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewAdapter.notifyDataSetChanged();
    }

    // todo extract this code
    private fun initializePlaces() {
        val apiKey = getString(R.string.api_key)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!.applicationContext, apiKey)
        }
    }
}
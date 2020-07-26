package com.example.pointme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointme.logic.CoroutineRunner
import com.example.pointme.logic.PlacesProxy
import com.example.pointme.platform.listeners.DestinationSelectionListener
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.settings.DistancePreferenceManager
import com.example.pointme.models.dtos.CompletedNavigationOperation
import com.example.pointme.platform.adapters.NavigationAdapter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : Fragment() {
    @Inject lateinit var destinationSelectionListener: DestinationSelectionListener
    @Inject lateinit var operationManager: NavigationOperationManager
    @Inject lateinit var preferenceManager: DistancePreferenceManager
    @Inject lateinit var coroutineRunner: CoroutineRunner
    @Inject lateinit var placesProxy: PlacesProxy

    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesProxy.initialize(
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment)

        coroutineRunner.run {
            val sessions = operationManager.getLastSessions(DEFAULT_SESSION_LIMIT)
            viewAdapter = NavigationAdapter(sessions, activity!!, preferenceManager)

            if (sessions.count() == 0) {
                setupGettingStarted()
            } else {
                setupPastNavigation()
            }
        }
    }

    private fun setupGettingStarted() {

    }

    private fun setupPastNavigation() {
        val viewManager = LinearLayoutManager(activity!!)

        coroutineRunner.onUiThread(activity!!, Runnable {
            activity!!.findViewById<RecyclerView>(R.id.previous_activities).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewAdapter.notifyDataSetChanged()
    }
}
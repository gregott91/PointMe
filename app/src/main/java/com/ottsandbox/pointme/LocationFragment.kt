package com.ottsandbox.pointme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottsandbox.pointme.logic.CoroutineRunner
import com.ottsandbox.pointme.logic.PlacesProxy
import com.ottsandbox.pointme.logic.managers.NavigationOperationManager
import com.ottsandbox.pointme.logic.settings.DistancePreferenceManager
import com.ottsandbox.pointme.platform.adapters.NavigationAdapter
import com.ottsandbox.pointme.platform.listeners.DestinationSelectionListener
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.ottsandbox.pointme.utility.DEFAULT_SESSION_LIMIT
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

        val placesFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        placesProxy.initialize(placesFragment)

        coroutineRunner.run {
            val sessions = operationManager.getLastSessions(DEFAULT_SESSION_LIMIT)
            viewAdapter = NavigationAdapter(sessions, activity!!, preferenceManager)

            if (sessions.count() == 0) {
                setupGettingStarted(placesFragment)
            } else {
                setupPastNavigation()
            }
        }
    }

    private fun setupGettingStarted(placesFragment: AutocompleteSupportFragment) {
        val startButton = activity!!.findViewById<Button>(R.id.getting_started_button) as Button
        startButton.setOnClickListener {
            placesProxy.openFragment(placesFragment)
        }
    }

    private fun setupPastNavigation() {
        val setupView = activity!!.findViewById<RelativeLayout>(R.id.getting_started_layout)
        setupView.visibility = View.GONE

        val viewManager = LinearLayoutManager(activity!!)

        coroutineRunner.onUiThread(Runnable {
            val recyclerView = activity!!.findViewById<RecyclerView>(R.id.previous_activities)
            recyclerView.visibility = View.VISIBLE

            recyclerView.apply {
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
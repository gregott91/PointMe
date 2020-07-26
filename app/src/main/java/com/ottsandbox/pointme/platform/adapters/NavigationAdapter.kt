package com.ottsandbox.pointme.platform.adapters

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.data.repositories.NavigationOperationRepository
import com.ottsandbox.pointme.logic.settings.DistancePreferenceManager
import com.ottsandbox.pointme.models.Coordinate
import com.ottsandbox.pointme.models.dtos.CompletedNavigationOperation
import com.ottsandbox.pointme.models.entities.NavigationOperation
import com.ottsandbox.pointme.utility.helpers.getDirectionInfo
import com.ottsandbox.pointme.utility.helpers.getDistanceInfo
import com.ottsandbox.pointme.utility.helpers.getShortDirectionFromAngle

class NavigationAdapter(
    private val operations: Array<CompletedNavigationOperation>,
    private val activity: Activity,
    private val preferenceManager: DistancePreferenceManager
) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView: TextView = listItemView.findViewById(R.id.place_name)
        val distanceTextView: TextView = listItemView.findViewById(R.id.navigation_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.view_navigation, parent, false)
        return ViewHolder(contactView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: NavigationAdapter.ViewHolder, position: Int) {
        val operation = operations[position]

        val startCoordinate = Coordinate(operation.startLatitude, operation.startLongitude)
        val endCoordinate = Coordinate(operation.endLatitude, operation.endLongitude)

        val preference = preferenceManager.getDistancePreference(activity.applicationContext)
        val directionInfo = getDirectionInfo(startCoordinate, endCoordinate)
        val distanceInfo = getDistanceInfo(startCoordinate, endCoordinate, preference)

        viewHolder.nameTextView.text = operation.destinationName
        viewHolder.distanceTextView.text = String.format(activity.resources.getString(R.string.place_name_subheader),
            operation.operationDate.toLocalDate().toString(),
            distanceInfo.distance,
            distanceInfo.units,
            directionInfo.direction
        )
    }

    override fun getItemCount(): Int {
        return operations.size
    }
}
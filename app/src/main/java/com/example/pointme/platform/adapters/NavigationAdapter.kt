package com.example.pointme.platform.adapters

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.pointme.R
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.utility.helpers.getShortDirectionFromAngle

class NavigationAdapter(private val operations: Array<NavigationOperation>, private val activity: Activity) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {
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
        val operation: NavigationOperation = operations[position]

        viewHolder.nameTextView.text = operation.destinationName
        viewHolder.distanceTextView.text = String.format(activity.resources.getString(R.string.place_name_subheader),
            operation.operationDate.toLocalDate().toString(),
            operation.distance,
            operation.direction
        )
    }

    override fun getItemCount(): Int {
        return operations.size
    }
}
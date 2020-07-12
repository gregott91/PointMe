package com.example.pointme.platform.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointme.R
import com.example.pointme.models.entities.NavigationOperation

class NavigationAdapter(private val operations: Array<NavigationOperation>) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView: TextView = listItemView.findViewById(R.id.contact_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.view_navigation, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: NavigationAdapter.ViewHolder, position: Int) {
        val contact: NavigationOperation = operations.get(position)
        val textView = viewHolder.nameTextView
        textView.text = contact.destinationName
    }

    override fun getItemCount(): Int {
        return operations.size
    }
}
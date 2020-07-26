package com.example.pointme.platform

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ToastDisplayer @Inject constructor(@ApplicationContext private val context: Context) {
    fun displayMessage(text: String) {
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }
}
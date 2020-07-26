package com.example.pointme.platform

import android.view.View
import androidx.fragment.app.Fragment
import com.example.pointme.models.MessageDuration
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class MessageDisplayer @Inject constructor(
    private val fragment: Fragment
) {
    fun displayMessage(text: String, duration: MessageDuration) {
        val snackbar = makeSnackbar(text, duration)

        snackbar.show()
    }

    fun displayMessageWithAction(text: String, actionText: String, action: () -> Unit) {
        val snackbar = makeSnackbar(text, MessageDuration.INDEFINITE)

        snackbar.setAction(actionText) {
            action()
            snackbar.dismiss()
        }

        snackbar.show()
    }

    private fun makeSnackbar(text: String, duration: MessageDuration): Snackbar {
        return Snackbar
            .make(fragment.view!!, text, convertDuration(duration))
    }

    private fun convertDuration(duration: MessageDuration): Int {
        return when (duration) {
            MessageDuration.INDEFINITE -> Snackbar.LENGTH_INDEFINITE
            MessageDuration.LONG -> Snackbar.LENGTH_LONG
            MessageDuration.SHORT -> Snackbar.LENGTH_SHORT
        }
    }
}
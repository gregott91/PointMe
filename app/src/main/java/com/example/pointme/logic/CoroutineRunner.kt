package com.example.pointme.logic

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineRunner @Inject constructor(private val fragment: Fragment) {
    private var context: CoroutineContext = EmptyCoroutineContext
    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun run(suspendedFun: suspend () -> Unit) {
        runBlocking(context) {
            withContext(dispatcher) {
                suspendedFun()
            }
        }
    }

    fun onUiThread(action: Runnable ) {
        fragment.activity!!.runOnUiThread(action)
    }
}
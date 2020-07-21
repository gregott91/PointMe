package com.example.pointme.logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineRunner(private var context: CoroutineContext = EmptyCoroutineContext, private var dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    fun run(suspendedFun: suspend () -> Unit) {
        runBlocking<Unit>(context) {
            withContext(dispatcher) {
                suspendedFun()
            }
        }
    }
}
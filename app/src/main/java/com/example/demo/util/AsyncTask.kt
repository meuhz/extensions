package com.example.demo.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AsyncTask {
    private val taskScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun launch(action: () -> Unit) {
        taskScope.launch {
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun launch(action: () -> Unit, done: () -> Unit) {
        taskScope.launch {
            try {
                action()
                withContext(Dispatchers.Main) {
                    done()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun <T> doAction(action: () -> T, onResult: (T) -> Unit) {
        taskScope.launch {
            try {
                val result = action()
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
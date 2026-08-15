package com.example.demo.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber.DebugTree
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class FileLogTree(private val file: File) : DebugTree() {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val logDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val logScope = CoroutineScope(logDispatcher + SupervisorJob())

    init {
        file.getParentFile()?.mkdirs()
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logScope.launch {
            // if (t != null) {
            //     message += " " + t.getMessage();
            // }
            try {
                file.appendText(format.format(Date()) + " " + typeInfo(priority) + " " + tag + ": " + message + "\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun typeInfo(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "D"
        }
    }
}

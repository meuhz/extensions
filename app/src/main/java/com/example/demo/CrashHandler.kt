package com.example.demo

import android.app.Application
import android.os.Build
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Crash to file
 */
internal class CrashHandler private constructor(private val application: Application) :
    Thread.UncaughtExceptionHandler {
    private val mNextHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        val datetime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(File(application.cacheDir, "crash"), "crash_$datetime.txt")
        try {
            PrintWriter(BufferedWriter(FileWriter(file))).use {
                it.println("brand: " + Build.BRAND)
                it.println("model: " + Build.MODEL)
                it.println("release: " + Build.VERSION.RELEASE)
                it.println("versionCode: " + UIApp.versionCode)
                it.println("versionName: " + UIApp.versionName)
                e.printStackTrace(it)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        mNextHandler?.uncaughtException(t, e)
    }

    companion object {
        fun register(application: Application) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(application))
        }
    }
}

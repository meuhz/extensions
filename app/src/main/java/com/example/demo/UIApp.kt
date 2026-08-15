package com.example.demo

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.os.SystemClock
import android.util.Log
import com.example.demo.database.MyDatabase
import com.example.demo.util.FileLogTree
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UIApp : Application() {
    companion object {
        lateinit var INSTANCE: UIApp
            private set

        val database: MyDatabase
            get() = INSTANCE.database

        val isDebuggable: Boolean
            get() = (INSTANCE.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        val packageInfo: PackageInfo by lazy {
            INSTANCE.packageManager.getPackageInfo(INSTANCE.packageName, 0)
        }

        val versionCode: Long
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        val versionName: String
            get() = packageInfo.versionName ?: ""
    }

    private lateinit var database: MyDatabase

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Log.d("UIApp", "onCreate")
        val start = SystemClock.elapsedRealtime()
        CrashHandler.register(this)
        MMKV.initialize(this)
        database = MyDatabase.create(this)

        if (isDebuggable) {
            val threadPolicyBuilder = ThreadPolicy.Builder()
            threadPolicyBuilder.detectNetwork()
            threadPolicyBuilder.detectCustomSlowCalls()
            threadPolicyBuilder.penaltyLog()
            StrictMode.setThreadPolicy(threadPolicyBuilder.build())

            val vmPolicyBuilder = VmPolicy.Builder()
            vmPolicyBuilder.detectActivityLeaks()
            vmPolicyBuilder.detectCleartextNetwork()
            StrictMode.setVmPolicy(vmPolicyBuilder.build())

            Timber.plant(Timber.DebugTree())

            val datetime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(File(cacheDir, "log"), "log_$datetime.txt")
            Timber.plant(FileLogTree(file))
        }

        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.registerDefaultNetworkCallback(network)
        } else {
            manager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),
                network
            )
        }

        val time = SystemClock.elapsedRealtime() - start
        Log.d("UIApp", "onCreate " + time + "ms")
    }

    private val network = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Timber.d("onAvailable: network=%s", network)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Timber.d("onLosing: network=%s, maxMsToLive=%s", network, maxMsToLive)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Timber.d("onLost: network=%s", network)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Timber.d("onUnavailable")
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Timber.d(
                "onCapabilitiesChanged: network=%s, networkCapabilities=%s",
                network,
                networkCapabilities
            )
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Timber.d(
                "onLinkPropertiesChanged: network=%s, linkProperties=%s",
                network,
                linkProperties
            )
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
            Timber.d("onBlockedStatusChanged: network=%s, blocked=%s", network, blocked)
        }
    }
}

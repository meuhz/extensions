package androidx.extensions

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build

val Application.packageInfo: PackageInfo
    get() = packageManager.getPackageInfo(packageName, 0)

val Application.versionCode: Long
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }

val Application.versionName: String
    get() = packageInfo.versionName ?: ""

val Application.isDebuggable: Boolean
    get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

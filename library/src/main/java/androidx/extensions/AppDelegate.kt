package androidx.extensions

import android.app.Application
import android.content.Context
import android.content.ContextWrapper

object AppDelegate : ContextWrapper(null) {
    val application: Application
        get() = baseContext as Application

    @JvmSynthetic
    internal fun initialize(context: Context) {
        attachBaseContext(context.applicationContext)
    }
}
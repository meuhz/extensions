package androidx.extensions

import android.content.Context
import androidx.startup.Initializer

class AppInitializer : Initializer<AppInitializer> {
    override fun create(context: Context): AppInitializer {
        AppDelegate.initialize(context)
        return this
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = arrayListOf()
}

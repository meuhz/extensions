package com.example.demo.util

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue

object ViewUtils {
    @JvmStatic
    fun dp2px(context: Context, value: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
    }

    @JvmStatic
    fun sp2px(context: Context, value: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics)
    }

    @JvmStatic
    fun px2dp(context: Context, pixelValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_DIP, pixelValue, metrics)
        } else {
            if (metrics.density == 0f) {
                0f
            } else {
                pixelValue / metrics.density
            }
        }
    }

    @JvmStatic
    fun px2sp(context: Context, pixelValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, pixelValue, metrics)
        } else {
            if (metrics.scaledDensity == 0f) {
                0f
            } else {
                pixelValue / metrics.scaledDensity
            }
        }
    }

    private var lastClickTime = 0L

    @JvmStatic
    @JvmOverloads
    fun isSingleClick(duration: Int = 1000): Boolean {
        val lastTime = lastClickTime
        val nowTime = SystemClock.uptimeMillis()
        lastClickTime = nowTime
        return nowTime - lastTime > duration
    }
}
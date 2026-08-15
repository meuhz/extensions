package com.example.demo.util

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class KeyboardWatcher private constructor(private val activity: Activity) : OnGlobalLayoutListener,
    ActivityLifecycleCallbacks {
    private var mStatusBarHeight = 0
    private var mSoftKeyboardOpened = false
    private var mContentView: View? = null
    private var mListener: SoftKeyboardListener? = null

    fun setListener(listener: SoftKeyboardListener) {
        mListener = listener

        mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT)
        mContentView!!.getViewTreeObserver().addOnGlobalLayoutListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.registerActivityLifecycleCallbacks(this)
        } else {
            activity.application.registerActivityLifecycleCallbacks(this)
        }

        ViewCompat.setOnApplyWindowInsetsListener(mContentView!!) { _, insets ->
            mStatusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            insets
        }
    }

    fun removeListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.unregisterActivityLifecycleCallbacks(this)
        } else {
            activity.application.unregisterActivityLifecycleCallbacks(this)
        }
        if (mContentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mContentView!!, null)
            mContentView!!.getViewTreeObserver().removeOnGlobalLayoutListener(this)
            mContentView = null
        }
        mListener = null
    }

    override fun onGlobalLayout() {
        val mContentView = mContentView!!
        val mListener = mListener!!
        val r = Rect()
        // r will be populated with the coordinates of your view that area still visible.
        mContentView.getWindowVisibleDisplayFrame(r)

        val height = mContentView.getRootView().height
        val heightDiff = height - (r.bottom - r.top)
        if (!mSoftKeyboardOpened && heightDiff > height / 4) {
            mSoftKeyboardOpened = true
            if ((activity.window.attributes.flags and FLAG_FULLSCREEN) != FLAG_FULLSCREEN) {
                mListener.onSoftKeyboardOpened(heightDiff - mStatusBarHeight)
            } else {
                mListener.onSoftKeyboardOpened(heightDiff)
            }
        } else if (mSoftKeyboardOpened && heightDiff < height / 4) {
            mSoftKeyboardOpened = false
            mListener.onSoftKeyboardClosed()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (this.activity === activity) {
            removeListener()
        }
    }

    companion object {
        @JvmStatic
        fun with(activity: Activity): KeyboardWatcher {
            return KeyboardWatcher(activity)
        }
    }

    interface SoftKeyboardListener {
        /**
         * 键盘弹出事件
         * 
         * @param keyboardHeight 软键盘高度
         */
        fun onSoftKeyboardOpened(keyboardHeight: Int)

        /**
         * 键盘收起事件
         */
        fun onSoftKeyboardClosed()
    }
}
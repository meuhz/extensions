package com.example.demo.util

import android.app.Activity
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object KeyboardUtil {
    @JvmStatic
    @JvmOverloads
    fun showSoftKeyboard(view: View, useWindowInsetsController: Boolean = true) {
        if (useWindowInsetsController) {
            val windowController = ViewCompat.getWindowInsetsController(view)
            if (windowController != null) {
                windowController.show(WindowInsetsCompat.Type.ime())
                return
            }
        }
        val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        imm?.showSoftInput(view, 0)
    }

    @JvmStatic
    @JvmOverloads
    fun hideSoftKeyboard(view: View, useWindowInsetsController: Boolean = true) {
        if (useWindowInsetsController) {
            val windowController = ViewCompat.getWindowInsetsController(view)
            if (windowController != null) {
                windowController.hide(WindowInsetsCompat.Type.ime())
                return
            }
        }
        val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 判断软键盘是否可见
     */
    @JvmStatic
    fun isSoftKeyboardVisible(activity: Activity): Boolean {
        val content = activity.findViewById<View>(android.R.id.content)
        val height = content.getRootView().height
        val r = Rect()
        content.getWindowVisibleDisplayFrame(r) // 获取应用内容显示区域
        val heightDiff = height - (r.bottom - r.top) // 未处理（显示状态栏时）状态栏的高度
        return heightDiff > height / 4
    }

    /**
     * 点击输入框外隐藏软键盘，需重写 dispatchTouchEvent 监听所有的触摸事件
     */
    @JvmStatic
    fun hideSoftKeyboardByClick(event: MotionEvent, focusView: View) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (!isClickView(focusView, event)) {
                hideSoftKeyboard(focusView)
            }
        }
    }

    private fun isClickView(view: View, event: MotionEvent): Boolean {
        val point = intArrayOf(0, 0)
        view.getLocationInWindow(point)
        val left = point[0]
        val top = point[1]
        val right = left + view.width
        val bottom = top + view.height
        val x = event.x
        val y = event.y
        return x > left && x < right && y > top && y < bottom
    }
}

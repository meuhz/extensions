package com.example.demo.activity

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import com.example.demo.R

class MainPopupWindow(context: Context) : PopupWindow(context) {
    private val arrow: ImageView

    init {
        val viewGroup = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val root = LayoutInflater.from(context).inflate(
            R.layout.popup_main, viewGroup, false
        )
        arrow = root.findViewById(R.id.ic_popup_arrow)
        setContentView(root)
    }

    override fun showAsDropDown(anchor: View) {
        super.showAsDropDown(anchor)
        // 计算basepopup中心与anchorview中心方位
        // e.g：算出gravity == Gravity.Left，意味着Popup显示在anchorView的左侧
        val gravity = anchor.foregroundGravity
        // 计算垂直位置
        val vertical = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val horizontal = gravity and Gravity.HORIZONTAL_GRAVITY_MASK

        if (vertical == Gravity.BOTTOM) {
            arrow.rotation = 180f
            arrow.translationX = 96 * arrow.resources.displayMetrics.density
        } else if (vertical == Gravity.TOP) {
            arrow.rotation = 0f
        }
    }
}

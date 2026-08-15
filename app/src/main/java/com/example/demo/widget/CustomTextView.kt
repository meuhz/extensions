package com.example.demo.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.demo.R

class CustomTextView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var drawableSize = 0

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CustomTextView, defStyleAttr, 0).apply {
            drawableSize = getDimensionPixelSize(R.styleable.CustomTextView_drawableSize, 0)
        }.recycle()

        updateCompoundDrawable()
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawables(
            limitDrawableSize(left),
            limitDrawableSize(top),
            limitDrawableSize(right),
            limitDrawableSize(bottom)
        )
    }

    override fun setCompoundDrawablesRelative(
        start: Drawable?,
        top: Drawable?,
        end: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawablesRelative(
            limitDrawableSize(start),
            limitDrawableSize(top),
            limitDrawableSize(end),
            limitDrawableSize(bottom)
        )
    }

    fun setDrawableSize(drawableSize: Int) {
        this.drawableSize = drawableSize
        updateCompoundDrawable()
    }

    fun getDrawableSize(): Int {
        return drawableSize
    }

    private fun updateCompoundDrawable() {
        var drawables = getCompoundDrawablesRelative()
        var start = drawables[0]
        var top = drawables[1]
        var end = drawables[2]
        var bottom = drawables[3]
        val hasRelativeDrawables = start != null || end != null
        if (hasRelativeDrawables) {
            setCompoundDrawablesRelative(start, top, end, bottom)
        } else {
            drawables = getCompoundDrawables()
            start = drawables[0]
            top = drawables[1]
            end = drawables[2]
            bottom = drawables[3]
            val hasDrawables = start != null || top != null || end != null || bottom != null
            if (hasDrawables) {
                setCompoundDrawables(start, top, end, bottom)
            }
        }
    }

    private fun limitDrawableSize(drawable: Drawable?): Drawable? {
        if (drawable == null) {
            return null
        }
        val width = if (drawableSize != 0) drawableSize else drawable.getIntrinsicWidth()
        val height = if (drawableSize != 0) drawableSize else drawable.getIntrinsicHeight()
        drawable.setBounds(0, 0, width, height)
        return drawable
    }
}
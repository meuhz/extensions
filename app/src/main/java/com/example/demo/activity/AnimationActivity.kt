package com.example.demo.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.example.demo.databinding.ActivityAnimationBinding
import timber.log.Timber
import kotlin.math.hypot

class AnimationActivity : BaseActivity() {
    private lateinit var binding: ActivityAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimationBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.dot.setOnClickListener { v ->
            val springForce = SpringForce(1f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM)
            val animx = SpringAnimation(v, DynamicAnimation.SCALE_X)
            val animy = SpringAnimation(v, DynamicAnimation.SCALE_Y)
            animx.setSpring(springForce)
            animy.setSpring(springForce)
            animx.setStartVelocity(500f)
            animy.setStartVelocity(500f)
            animx.setMaxValue(1.5f)
            animy.setMaxValue(1.5f)
            animx.start()
            animy.start()
        }

        binding.dot2.setOnClickListener { v ->
            val set = AnimatorSet()
            set.play(ObjectAnimator.ofFloat(v, View.SCALE_X, 1f, 1.5f, 1f))
                .with(ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f, 1.5f, 1f))
            // set.setDuration(300);
            set.interpolator = OvershootInterpolator()
            set.start()
        }

        touchFling()
    }

    private fun circularReveal(view: View) {
        if (view.visibility == View.GONE) {
            circularRevealIn(view)
            fadeIn(view, 300)
        } else {
            circularRevealOut(view, 300, View.GONE)
            fadeOut(view, 300, View.GONE)
        }
    }

    private fun spring(view: View?) {
        // 弹簧动画
        val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y)
        val springForce = SpringForce()
            .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
            .setStiffness(SpringForce.STIFFNESS_LOW)
            .setFinalPosition(0f)
        anim.setSpring(springForce)
        anim.setStartVelocity(5000f)
        anim.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchFling() {
        val detector = GestureDetector(this, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent): Boolean {
                Timber.d("onDown: x=%s, y=%s", e.rawX, e.rawY)
                return true
            }

            override fun onShowPress(e: MotionEvent) {
                Timber.d("onShowPress: x=%s, y=%s", e.rawX, e.rawY)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                Timber.d("onSingleTapUp: x=%s, y=%s", e.rawX, e.rawY)
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                Timber.d("onLongPress: x=%s, y=%s", e.rawX, e.rawY)
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                Timber.d("onScroll: distanceX=%s, distanceY=%s", distanceX, distanceY)
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                Timber.d("onFling: velocityX=%s, velocityY=%s", velocityX, velocityY)
                scrollFling(binding.hScrollView, -velocityX)
                return true
            }
        })

        binding.text.setOnTouchListener { v, event ->
            detector.onTouchEvent(event!!)
        }
    }

    private fun scrollFling(view: View, velocityX: Float) {
        // 投掷动画 水平滑动ScrollView
        val fling = FlingAnimation(view, DynamicAnimation.SCROLL_X)
        fling.setStartVelocity(velocityX).start()
    }


    /**
     * 圆形揭露动画显示 View
     */
    private fun circularRevealIn(view: View, duration: Long = 300) {
        if (view.visibility != View.VISIBLE) {
            val cx = view.width / 2
            val cy = view.height / 2
            val radius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, radius)
            if (duration > 0) {
                anim.duration = duration
            }
            anim.start()
            view.visibility = View.VISIBLE
        }
    }

    /**
     * 圆形揭露动画隐藏 View
     */
    private fun circularRevealOut(view: View, duration: Long, visibility: Int) {
        if (view.visibility == View.VISIBLE) {
            val cx = view.width / 2
            val cy = view.height / 2
            val radius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0f)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = visibility
                }
            })
            if (duration > 0) {
                anim.duration = duration
            }
            anim.start()
        }
    }

    /**
     * 淡入动画
     */
    private fun fadeIn(view: View, duration: Long) {
        if (view.visibility != View.VISIBLE) {
            view.setAlpha(0f)
            view.visibility = View.VISIBLE
            val anim = view.animate().alpha(1f).setListener(null)
            if (duration > 0) {
                anim.setDuration(duration)
            }
            anim.start()
        }
    }

    /**
     * 淡出动画
     */
    private fun fadeOut(view: View, duration: Long, visibility: Int) {
        if (view.visibility == View.VISIBLE) {
            val anim = view.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = visibility
                }
            })
            if (duration > 0) {
                anim.setDuration(duration)
            }
            anim.start()
        }
    }
}

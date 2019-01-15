package com.xiaoe.shop.wxb.business.video.ui

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.xiaoe.shop.wxb.R
import zqx.rj.com.mvvm.common.layoutInflater

/**
 * Date: 2019/1/14 17:31
 * Author: hans yang
 * Description:
 */
class VideoTimerTipsViewHelper(val context: Context, private val parentView: View){

    var isShowTimerTips = false
    private var mTimerTipsView: View
    private var mTimerTipsTime: TextView
    var closeButtonClick: ((Unit)->Unit) ?= null

    init {
        val contentView = context.layoutInflater(R.layout.layout_timer_tips)
        mTimerTipsView = contentView.findViewById(R.id.timer_tips_view)
        mTimerTipsTime = contentView.findViewById(R.id.timer_tips_time) as TextView
        contentView.findViewById(R.id.timer_tips_close).setOnClickListener {
            closeButtonClick?.invoke(Unit)
            dismissTimerTips()
        }
        mTimerTipsView.setOnClickListener {
            dismissTimerTips()
        }
    }

    fun updateTimeValue(time: Long){
        if (isShowTimerTips) {
            mTimerTipsTime?.let {
                mTimerTipsTime.text = String.format("${if (time < 10) "0" else ""}%ds", time)
            }
        }
    }

    fun showTimerTips() {
        if (!isShowTimerTips && parentView != null) {

            val animation = AnimationUtils.loadAnimation(context,R.anim.slide_right_in)
            animation.interpolator = DecelerateInterpolator()
            animation.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
            if (parentView is FrameLayout){
                parentView.removeAllViews()
                parentView.addView(mTimerTipsView)
            }

            mTimerTipsView.clearAnimation()
            mTimerTipsView.startAnimation(animation)
            isShowTimerTips = true
        }

    }

    fun dismissTimerTips() {
        if (isShowTimerTips && parentView != null) {
            val animation = AnimationUtils.loadAnimation(context,R.anim.slide_right_out)
            animation.interpolator = DecelerateInterpolator()
            animation.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (parentView is FrameLayout)    parentView.removeAllViews()
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
            mTimerTipsView.clearAnimation()
            mTimerTipsView.startAnimation(animation)
            isShowTimerTips = false
        }
    }
}
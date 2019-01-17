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
import kotlin.collections.ArrayList

/**
 * Date: 2019/1/14 17:31
 * Author: sharkChen
 * Description:
 */
class VideoSpeedPlayViewHelper(val context: Context, private val parentView: View) : View.OnClickListener {

    // 倍速选择view
    private var mSpeedPlayView: View

    // 可选择倍率View数组
    private val mSpeedPlayTexts: ArrayList<TextView> by lazy {
        ArrayList<TextView>()
    }

    var speedClickBlock: ((Int)-> Int)? = null

    init {
        val contentView = context.layoutInflater(R.layout.layout_speed_play)
        mSpeedPlayView = contentView.findViewById(R.id.speed_play_view)
        mSpeedPlayView.setOnClickListener(this@VideoSpeedPlayViewHelper)  // 背景点击事件

        mSpeedPlayTexts.add(contentView.findViewById(R.id.btn_speed_play_0) as TextView)
        mSpeedPlayTexts.add(contentView.findViewById(R.id.btn_speed_play_1) as TextView)
        mSpeedPlayTexts.add(contentView.findViewById(R.id.btn_speed_play_2) as TextView)
        mSpeedPlayTexts.add(contentView.findViewById(R.id.btn_speed_play_3) as TextView)
        mSpeedPlayTexts.add(contentView.findViewById(R.id.btn_speed_play_4) as TextView)

        // 为每个选项增加点击事件
        for (i in mSpeedPlayTexts.indices) {
            mSpeedPlayTexts.get(i).setOnClickListener(this@VideoSpeedPlayViewHelper)
        }
    }

    // 展示view
    fun showSpeedPlayView() {
        if (parentView != null) {
            if (parentView is FrameLayout){
                parentView.removeAllViews()
                parentView.addView(mSpeedPlayView)
            }
        }
    }

    // 隐藏
    fun hiddenSpeedPlayView() {
        if (parentView != null) {
            if (parentView is FrameLayout){
                parentView.removeAllViews()
            }
        }
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when(v?.id){
            R.id.speed_play_view->{
                speedClickBlock?.invoke(-1)
            }
            R.id.btn_speed_play_0->{
                speedClickBlock?.invoke(0)
            }
            R.id.btn_speed_play_1->{
                speedClickBlock?.invoke(1)
            }
            R.id.btn_speed_play_2->{
                speedClickBlock?.invoke(2)
            }
            R.id.btn_speed_play_3->{
                speedClickBlock?.invoke(3)
            }
            R.id.btn_speed_play_4->{
                speedClickBlock?.invoke(4)
            }
        }
    }
}





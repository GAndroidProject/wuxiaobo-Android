package com.xiaoe.shop.wxb.business.audio.presenter

import android.os.CountDownTimer
import com.xiaoe.shop.wxb.utils.LogUtils

/**
 * Date: 2018/12/24 15:58
 * Author: hans yang
 * Description: 倒计时工具
 */
class CountDownTimerTool{

    private val any = Any()

    var mMillisUntilFinished : Long = -1

    private val defaultInterval = 1000L

    private var mCountDownTimer : CountDownTimer ?= null
    var mCountDownCallBack : CountDownCallBack ?= null

    fun countDown(time: Int,callBack: CountDownCallBack ?= null,interval: Long = defaultInterval){
        release()
        mCountDownCallBack = callBack
        mCountDownTimer = object : CountDownTimer((time * 60 * 1000).toLong(), interval){

            override fun onFinish() {
                mCountDownCallBack?.onFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                mMillisUntilFinished = millisUntilFinished
                mCountDownCallBack?.onTick(millisUntilFinished)
            }
        }.apply { start() }
    }

    fun release() {
        synchronized(any){
            mCountDownTimer?.apply {
                mCountDownCallBack = null
                mMillisUntilFinished = -1
                cancel()
                mCountDownTimer = null
            }
            LogUtils.d("release mCountDownTimer = $mCountDownTimer" )

            //和上面代码等价的java方式写法
//            if (mCountDownTimer != null) {
//                mCountDownTimer?.cancel()
//                mCountDownCallBack = null
//                mMillisUntilFinished = -1
//                mCountDownTimer = null
//            }
        }
    }

    interface CountDownCallBack{
        fun onTick(millisUntilFinished : Long)
        fun onFinish()
    }

}
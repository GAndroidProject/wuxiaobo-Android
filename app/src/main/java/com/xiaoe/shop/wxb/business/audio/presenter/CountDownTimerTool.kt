package com.xiaoe.shop.wxb.business.audio.presenter

import android.os.CountDownTimer
import com.xiaoe.shop.wxb.utils.LogUtils

/**
 * Date: 2018/12/24 15:58
 * Author: hans yang
 * Description: 倒计时工具
 */
class CountDownTimerTool{
    val mTag = "CountDownTimerTool"

    private val any = Any()

    var mMillisUntilFinished : Long = -1

    private val defaultInterval = 1000L

    private var mCountDownTimer : CountDownTimer ?= null
    var mCountDownCallBack : CountDownCallBack ?= null
    var isRunning = false

    fun countDown(time: Int,callBack: CountDownCallBack ?= null,interval: Long = defaultInterval){
        countDown((time * 60 * 1000).toLong(),callBack,interval)
    }
    fun countDown(millisTime: Long,callBack: CountDownCallBack ?= null,interval: Long = defaultInterval){
        release()
        mCountDownCallBack = callBack
        start(millisTime, interval)
    }

    private fun start(millisTime: Long, interval: Long = defaultInterval) {
        mCountDownTimer = object : CountDownTimer(millisTime, interval) {

            override fun onFinish() {
                LogUtils.d("$mTag--onFinish---  millisUntilFinished = $mMillisUntilFinished")
                isRunning = false
                mCountDownCallBack?.onFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                LogUtils.d("$mTag--onTick---  millisUntilFinished = $millisUntilFinished")
                isRunning = true
                mMillisUntilFinished = millisUntilFinished
                mCountDownCallBack?.onTick(millisUntilFinished)
            }
        }.apply { start() }
        isRunning = true
    }

    fun release(isPause: Boolean = false) {
        synchronized(any){
            mCountDownTimer?.apply {
                if (!isPause)    mCountDownCallBack = null
//                mMillisUntilFinished = -1
                cancel()
                mCountDownTimer = null
                isRunning = false
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
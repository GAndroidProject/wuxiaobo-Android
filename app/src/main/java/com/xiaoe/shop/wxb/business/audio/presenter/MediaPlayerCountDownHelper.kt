package com.xiaoe.shop.wxb.business.audio.presenter

import com.xiaoe.common.app.XiaoeApplication
import com.xiaoe.shop.wxb.business.audio.presenter.CountDownTimerTool.CountDownCallBack
import java.lang.Exception

/**
 * Date: 2018/12/24 15:28
 * Author: hans yang
 * Description: 音视频倒计时需求帮助类
 */
object MediaPlayerCountDownHelper{

    const val COUNT_DOWN_STATE_CLOSE = 1
    const val COUNT_DOWN_STATE_TIME = 2
    const val COUNT_DOWN_STATE_CURRENT = 3

    const val COUNT_DOWN_DURATION_10 = 10
    const val COUNT_DOWN_DURATION_20 = 20
    const val COUNT_DOWN_DURATION_30 = 30
    const val COUNT_DOWN_DURATION_60 = 60

    private var mCurrentTime = -1
    var mCurrentState = COUNT_DOWN_STATE_CLOSE
    var mAudioSelectedPosition = 0
    var mVideoSelectedPosition = 0
    var mCountDownCallBack : CountDownCallBack ?= null

    var remainTime = 0L//剩余倒计时时间
    var pauseStateSystemTime = 0L//暂停计时器时候的系统时间
    var isAtAudioNewActivity = false

    private val mHandler by lazy {
        XiaoeApplication.applicationHandler
    }

    private val mCountDownTimerTool : CountDownTimerTool by lazy {
        CountDownTimerTool()
    }

    private val countDownCallBack: CountDownCallBack by lazy {
        object : CountDownCallBack{
            override fun onTick(millisUntilFinished: Long) {
                mCountDownCallBack?.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                closeCountDownTimer()
                mCountDownCallBack?.onFinish()
            }
        }
    }

    private fun setSelectedPosition(duration: Int) {
        when (duration) {
            COUNT_DOWN_DURATION_10 -> {
                mAudioSelectedPosition = 2
                mVideoSelectedPosition = 2
            }
            COUNT_DOWN_DURATION_20 -> {
                mAudioSelectedPosition = 3
                mVideoSelectedPosition = 2
            }
            COUNT_DOWN_DURATION_30 -> {
                mAudioSelectedPosition = 4
                mVideoSelectedPosition = 2
            }
            COUNT_DOWN_DURATION_60 -> {
                mAudioSelectedPosition = 5
                mVideoSelectedPosition = 3
            }
        }
    }

    fun startCountDown(duration : Int,callBack : CountDownCallBack?= null){
        setSelectedPosition(duration)
        mCountDownCallBack = callBack

        mCurrentState = COUNT_DOWN_STATE_TIME
        mCurrentTime = duration

        mCountDownTimerTool.countDown(duration,countDownCallBack)
        cancelCloseAudioPlayRunnable()
    }

    fun choiceCurrentPlayFinished(){
        mAudioSelectedPosition = 1
        mVideoSelectedPosition = 1
        mCurrentState = COUNT_DOWN_STATE_CURRENT
        mCountDownTimerTool.release()
        cancelCloseAudioPlayRunnable()
    }

    fun closeCountDownTimer(){
        mAudioSelectedPosition = 0
        mVideoSelectedPosition = 0
        mCurrentState = COUNT_DOWN_STATE_CLOSE
        mCountDownTimerTool.release()
        cancelCloseAudioPlayRunnable()
    }

    fun resumeCountDownTimer(){
        if (COUNT_DOWN_STATE_TIME == mCurrentState && !mCountDownTimerTool.isRunning){
            val duration = remainTime - (System.currentTimeMillis() - pauseStateSystemTime)
            if (duration > 1000 && duration <= mCountDownTimerTool.mMillisUntilFinished)
                mCountDownTimerTool.countDown(duration, countDownCallBack)
            else    closeCountDownTimer()
            cancelCloseAudioPlayRunnable()
        }
    }

    fun pauseCountDownTimer(){
        if (COUNT_DOWN_STATE_TIME == mCurrentState && mCountDownTimerTool.isRunning){
            mCountDownTimerTool.release(true)
            pauseStateSystemTime = System.currentTimeMillis()
            remainTime = mCountDownTimerTool.mMillisUntilFinished

            cancelCloseAudioPlayRunnable()
            if (remainTime > 0)
                mHandler.postDelayed(mCloseAudioPlayRunnable, remainTime)
        }
    }

    private fun cancelCloseAudioPlayRunnable() {
        mHandler.removeCallbacks(mCloseAudioPlayRunnable)
    }

    private val mCloseAudioPlayRunnable = Runnable {
        try {
            if (AudioMediaPlayer.isPlaying()) {
                AudioMediaPlayer.play()
            }
            closeCountDownTimer()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun onViewDestroy(){
        mCountDownCallBack = null
    }

    fun getCountText() : String{
        return getCountText(mCountDownTimerTool.mMillisUntilFinished)
    }

    private fun getCountText(time : Long) : String{
        var text = ""
        if (time >= 0){
            text = "%s:%s"
            var minute = time/(60 * 1000)
            var second = if (minute <= 0) time /1000 else  time%(minute * 60 * 1000)/1000
            text = String.format(text, formatString(minute),formatString(second))
        }
        return text
    }

    private fun formatString(time: Long) = if (time < 10) "0$time" else time.toString()

}


package com.xiaoe.shop.wxb.business.audio.presenter

/**
 * Date: 2018/12/24 15:28
 * Author: hans yang
 * Description: 音视频倒计时需求帮助类
 */
object MediaPlayerCountDownHelper{

    const val COUNT_DOWN_STATE_CLOSE = 1
    const val COUNT_DOWN_STATE_TIME = 2
    const val COUNT_DOWN_STATE_CURRENT = 3

    const val COUNT_DOWN_DURATION_10 = 1
    const val COUNT_DOWN_DURATION_20 = 2
    const val COUNT_DOWN_DURATION_30 = 3
    const val COUNT_DOWN_DURATION_60 = 6

    var mCurrentTime = -1
    var mCurrentState = COUNT_DOWN_STATE_CLOSE
    var mAudioSelectedPosition = 0
    var mVideoSelectedPosition = 0

//    fun startCountDown(time : Int){
//        mCurrentTime = time
//        CountDownTimerTool.countDown(time)
//    }

    fun startCountDown(duration : Int,callBack : CountDownTimerTool.CountDownCallBack?= null){
        setSelectedPosition(duration)
        mCurrentState = COUNT_DOWN_STATE_TIME
        mCurrentTime = duration
        CountDownTimerTool.countDown(duration,callBack)
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

    fun setCountDownCallBack(callBack : CountDownTimerTool.CountDownCallBack){
        CountDownTimerTool.mCountDownCallBack = callBack
    }

    fun choiceCurrentPlayFinished(){
        mAudioSelectedPosition = 1
        mVideoSelectedPosition = 1
        mCurrentState = COUNT_DOWN_STATE_CURRENT
        CountDownTimerTool.release()
    }

    fun closeCountDownTimer(){
        mAudioSelectedPosition = 0
        mVideoSelectedPosition = 0
        mCurrentState = COUNT_DOWN_STATE_CLOSE
        CountDownTimerTool.release()
    }

    fun onViewDestroy(){
        CountDownTimerTool.mCountDownCallBack = null
    }

    fun getCountText(time : Long) : String{
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


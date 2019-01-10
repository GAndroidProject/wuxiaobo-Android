package com.xiaoe.shop.wxb.adapter.audio

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xiaoe.common.entitys.AudioPlayEntity
import com.xiaoe.common.utils.DateFormat
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer

/**
 * Date: 2018/12/29 10:08
 * Author: hans yang
 * Description:
 */
class AudioPlayListNewAdapter(layoutId : Int) : BaseQuickAdapter<AudioPlayEntity,BaseViewHolder>(layoutId){

    override fun convert(helper: BaseViewHolder?, item: AudioPlayEntity?) {
        helper?.apply {
            item?.apply{
                setText(R.id.item_title,title)
                setText(R.id.item_duration,DateFormat.longToString(totalDuration * 1000L))
                val color = if(resourceId == AudioMediaPlayer.getAudio().resourceId)
                    ContextCompat.getColor(mContext,R.color.high_title_color)
                else ContextCompat.getColor(mContext,R.color.secondary_title_color)
                setTextColor(R.id.item_title,color)
                setTextColor(R.id.item_duration,color)
            }
        }
    }
}

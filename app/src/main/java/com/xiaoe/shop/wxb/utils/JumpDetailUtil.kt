package com.xiaoe.shop.wxb.utils

import android.content.Context
import com.xiaoe.common.app.CommonUserInfo
import com.xiaoe.common.entitys.ResourceType.TYPE_AUDIO
import com.xiaoe.common.entitys.ResourceType.TYPE_BIG_COLUMN
import com.xiaoe.common.entitys.ResourceType.TYPE_COLUMN
import com.xiaoe.common.entitys.ResourceType.TYPE_MEMBER
import com.xiaoe.common.entitys.ResourceType.TYPE_SUPER_MEMBER
import com.xiaoe.common.entitys.ResourceType.TYPE_TEXT
import com.xiaoe.common.entitys.ResourceType.TYPE_VIDEO
import com.xiaoe.shop.wxb.common.JumpDetail

/**
 * Date: 2019/1/3 11:32
 * Author: hans yang
 * Description:
 */

fun jumpKnowledgeDetail(context: Context, srcType: Int, resId: String, imgUrl: String){

    when (srcType) {
        TYPE_TEXT -> JumpDetail.jumpImageText(context, resId, imgUrl, "") // 图文
        TYPE_AUDIO -> JumpDetail.jumpAudio(context, resId, 0) // 音频
        TYPE_VIDEO -> JumpDetail.jumpVideo(context, resId, "", false, "") // 视频
        TYPE_MEMBER -> JumpDetail.jumpColumn(context, resId, imgUrl, srcType) // 会员
        TYPE_COLUMN -> JumpDetail.jumpColumn(context, resId, imgUrl, srcType) // 专栏
        TYPE_BIG_COLUMN -> JumpDetail.jumpColumn(context, resId, imgUrl, srcType) // 大专栏
        TYPE_SUPER_MEMBER ->{
            if (CommonUserInfo.isIsSuperVipAvailable()) {
                JumpDetail.jumpSuperVip(context)
            } else {
                ToastUtils.show(context, "app 暂不支持非一年规格的会员购买")
            }
        }
    }
}
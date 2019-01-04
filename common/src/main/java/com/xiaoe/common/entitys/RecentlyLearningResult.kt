package com.xiaoe.common.entitys

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_AUDIO
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_DEFAULT

/**
 * Date: 2018/12/27 19:27
 * Author: hans yang
 * Description:
 */

data class RecentlyLearning(val name : String) : MultiItemEntity {
    override fun getItemType() = if (name.length < 3)   ITEM_TYPE_DEFAULT else ITEM_TYPE_AUDIO
}
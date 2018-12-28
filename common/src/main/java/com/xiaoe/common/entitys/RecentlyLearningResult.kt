package com.xiaoe.common.entitys

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xiaoe.common.entitys.ItemType.Companion.type_audio
import com.xiaoe.common.entitys.ItemType.Companion.type_default

/**
 * Date: 2018/12/27 19:27
 * Author: hans yang
 * Description:
 */

data class RecentlyLearning(val name : String) : MultiItemEntity {
    override fun getItemType() = if (name.length < 3)   type_default else type_audio
}
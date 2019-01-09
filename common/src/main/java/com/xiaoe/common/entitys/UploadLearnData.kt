package com.xiaoe.common.entitys

import com.google.gson.annotations.SerializedName

/**
 * Date: 2019/1/8 11:29
 * Author: hans yang
 * Description:
 */
data class UploadDataParam(@SerializedName("is_single_item")
                           var isSingleItem: Boolean = true,
                           @SerializedName("resource_id")
                           val resourceId: String = "",
                           @SerializedName("resource_type")
                           val resourceType: Int = 0,
                           @SerializedName("learn_progress")
                           val progress: Int = 10,
                           @SerializedName("org_learn_progress")
                           var orgLearnProgress: String = "0",
                           @SerializedName("is_reTry")
                           var isReTry: Boolean = false,
                           @SerializedName("agent_type")
                           val agentType: Int = 2,
                           @SerializedName("spend_time")
                           val spendTime: Int = 0)



data class UploadHistory(@SerializedName("type")
                         val type: Int = 0,
                         @SerializedName("id")
                         val id: String = "",
                         @SerializedName("historylist")
                         val historyList: HashMap<String,History>?= null,
                         @SerializedName("title")
                         val title: String = "")

data class History(@SerializedName("type")
                   val type: Int = 0,
                   @SerializedName("process")
                   val process: Int = 0,
                   @SerializedName("time")
                   val time: String = System.currentTimeMillis().toString()


)
data class SingleItemLearnProgress(@SerializedName("type")
                             val type: Int = 0,
                             @SerializedName("playTime")
                             val playTime: Int = 0,
                             @SerializedName("process")
                             val process: Int = 0,
                             @SerializedName("time")
                             val time: String = System.currentTimeMillis().toString())
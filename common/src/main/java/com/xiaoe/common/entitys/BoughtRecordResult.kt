package com.xiaoe.common.entitys


import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
import com.xiaoe.common.entitys.ItemType.Companion.type_audio
import com.xiaoe.common.entitys.ItemType.Companion.type_default

data class BoughtRecord(@SerializedName("msg")
                         val msg: String = "",
                        @SerializedName("code")
                         val code: Int = 0,
                        @SerializedName("data")
                         val data: Data? = null)


data class AllDataItem(@SerializedName("share_user_id")
                       val shareUserId: String = "",
                       @SerializedName("agent")
                       val agent: String = "",
                       @SerializedName("is_member_remind")
                       val isMemberRemind: Int = 0,
                       @SerializedName("resource_type")
                       val resourceType: Int = 0,
                       @SerializedName("created_at")
                       val createdAt: String = "",
                       @SerializedName("remark")
                       val remark: String = "",
                       @SerializedName("wx_app_type")
                       val wxAppType: Int = 0,
                       @SerializedName("generate_type")
                       val generateType: Int = 0,
                       @SerializedName("payment_type")
                       val paymentType: Int = 0,
                       @SerializedName("is_deleted")
                       val isDeleted: Int = 0,
                       @SerializedName("share_type")
                       val shareType: Int = 0,
                       @SerializedName("updated_at")
                       val updatedAt: String = "",
                       @SerializedName("user_id")
                       val userId: String = "",
                       @SerializedName("img_url")
                       val imgUrl: String = "",
                       @SerializedName("content_app_id")
                       val contentAppId: String = "",
                       @SerializedName("price")
                       val price: Int = 0,
                       @SerializedName("product_id")
                       val productId: String = "",
                       @SerializedName("resource_id")
                       val resourceId: String = "",
                       @SerializedName("need_notify")
                       val needNotify: Int = 0,
                       @SerializedName("expire_at")
                       val expireAt: String = "",
                       @SerializedName("purchase_name")
                       val purchaseName: String = "",
                       @SerializedName("app_id")
                       val appId: String = "",
                       @SerializedName("channel_id")
                       val channelId: String = "",
                       @SerializedName("order_id")
                       val orderId: String = "") : MultiItemEntity{
    override fun getItemType(): Int
        = if(2 == resourceType) type_audio else type_default
}


data class Data(@SerializedName("allData")
                val allData: List<AllDataItem>?)



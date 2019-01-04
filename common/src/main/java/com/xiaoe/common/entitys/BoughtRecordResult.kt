package com.xiaoe.common.entitys

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_AUDIO
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_DEFAULT

data class DataItem(@SerializedName("resource_type")
                    val resourceType: Int = 0,
                    @SerializedName("created_at")
                    val createdAt: String = "",
                    @SerializedName("purchased_goods_display")
                    val purchasedGoodsDisplay: String = "",
                    @SerializedName("payment_type")
                    val paymentType: Int = 0,
                    @SerializedName("updated_at")
                    val updatedAt: String = "",
                    @SerializedName("img_url")
                    val imgUrl: String = "",
                    @SerializedName("content_app_id")
                    val contentAppId: String = "",
                    @SerializedName("price")
                    val price: Int = 0,
                    @SerializedName("product_id")
                    val productId: String = "",
                    @SerializedName("purchased_goods_type")
                    val purchasedGoodsType: Int = 0,
                    @SerializedName("resource_id")
                    val resourceId: String = "",
                    @SerializedName("expire_at")
                    val expireAt: String = "",
                    @SerializedName("purchase_name")
                    val purchaseName: String = "",
                    @SerializedName("app_id")
                    val appId: String = ""): MultiItemEntity {

    override fun getItemType(): Int
            = if(2 == resourceType) ITEM_TYPE_AUDIO else ITEM_TYPE_DEFAULT
}


data class BoughtRecord(@SerializedName("msg")
                        val msg: String = "",
                        @SerializedName("code")
                        val code: Int = 0,
                        @SerializedName("data")
                        val data: MutableList<DataItem>?= null)



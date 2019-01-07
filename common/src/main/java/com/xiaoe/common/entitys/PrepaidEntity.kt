package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

/**
 * 预支付下单信息实体 bean
 */
data class PrepaidEntity(@SerializedName("msg")
                         val msg: String = "",
                         @SerializedName("code")
                         val code: Int = 0,
                         @SerializedName("data")
                         val data: PrepaidEntityData)


data class PrepaidEntityData(@SerializedName("balance")
                val balance: Int = 0,
                @SerializedName("updated_at")
                val updatedAt: String = "",
                @SerializedName("user_id")
                val userId: String = "",
                @SerializedName("price")
                val price: Int = 0,
                @SerializedName("product_id")
                val productId: String = "",
                @SerializedName("created_at")
                val createdAt: String = "",
                @SerializedName("id")
                val id: Int = 0,
                @SerializedName("order_id")
                val orderId: String = "",
                @SerializedName("app_id")
                val appId: String = "")



package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

/**
 * 可充值列表实体 bean
 */
data class AmountItemEntity(@SerializedName("msg")
                            val msg: String = "",
                            @SerializedName("code")
                            val code: Int = 0,
                            @SerializedName("data")
                            val data: List<AmountDataItem>?)


data class AmountDataItem(@SerializedName("balance")
                    var balance: Int = 0,
                          @SerializedName("price")
                    var price: Int = 0,
                          @SerializedName("product_id")
                    val productId: String = "")



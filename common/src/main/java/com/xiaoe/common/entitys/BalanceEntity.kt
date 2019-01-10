package com.xiaoe.common.entitys


import com.alibaba.fastjson.JSONObject
import com.google.gson.annotations.SerializedName

/**
 * 波豆余额实体类
 */
data class BalanceEntity(@SerializedName("msg")
                         val msg: String = "",
                         @SerializedName("code")
                         val code: Int = 0,
                         @SerializedName("data")
                         val data: BalanceData)


data class BalanceData(@SerializedName("balance")
                       val balance: JSONObject)



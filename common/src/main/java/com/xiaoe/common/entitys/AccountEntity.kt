package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

data class AeDataItem(@SerializedName("amount")
                      val amount: Int = 0,
                      @SerializedName("flow_type")
                      val flowType: Int = 0,
                      @SerializedName("wallet_type")
                      val walletType: Int = 0,
                      @SerializedName("created_at")
                      val createdAt: String = "",
                      @SerializedName("remark")
                      val remark: String = "")


data class AeData(@SerializedName("per_page")
                  val perPage: Int = 0,
                  @SerializedName("total")
                  val total: Int = 0,
                  @SerializedName("data")
                  val data: List<AeDataItem>?,
                  @SerializedName("last_page")
                  val lastPage: Int = 0,
                  @SerializedName("next_page_url")
                  val nextPageUrl: String = "",
                  @SerializedName("from")
                  val from: Int = 0,
                  @SerializedName("to")
                  val to: Int = 0,
                  @SerializedName("current_page")
                  val currentPage: Int = 0)


data class AccountEntity(@SerializedName("msg")
                         val msg: String = "",
                         @SerializedName("code")
                         val code: Int = 0,
                         @SerializedName("data")
                         val data: AeData)



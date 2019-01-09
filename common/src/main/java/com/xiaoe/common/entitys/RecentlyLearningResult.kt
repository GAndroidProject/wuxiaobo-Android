package com.xiaoe.common.entitys


import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName

data class GoodsListItem(@SerializedName("resource_type")
                         val resourceType: Int = 0,
                         @SerializedName("resource_id")
                         val resourceId: String = "",
                         @SerializedName("is_finish")
                         val isFinish: Int = 0,
                         @SerializedName("org_learn_progress")
                         val orgLearnProgress: String = "",
                         @SerializedName("learn_progress")
                         val learnProgress: Int = 0,
                         @SerializedName("last_learn_time")
                         val lastLearnTime: String = "",
                         @SerializedName("info")
                         val info: Info): MultiItemEntity {
    override fun getItemType(): Int
            = if(2 == resourceType) ItemType.ITEM_TYPE_AUDIO else ItemType.ITEM_TYPE_DEFAULT
}


data class Data(@SerializedName("good_list")
                val goodsList: MutableList<GoodsListItem>?= null)


data class Info(@SerializedName("line_price")
                val linePrice: Int = 0,
                @SerializedName("author")
                val author: String = "",
                @SerializedName("is_try")
                val isTry: Int = 0,
                @SerializedName("goods_id")
                val goodsId: String = "",
                @SerializedName("sale_status")
                val saleStatus: Int = 0,
                @SerializedName("img_url_compressed")
                val imgUrlCompressed: String = "",
                @SerializedName("title")
                val title: String = "",
                @SerializedName("alive_start_at")
                val aliveStartAt: String = "",
                @SerializedName("img_url")
                val imgUrl: String = "",
                @SerializedName("price")
                val price: Int = 0,
                @SerializedName("is_expire")
                val isExpire: Int = 0,
                @SerializedName("org_summary")
                val orgSummary: String = "",
                @SerializedName("periodical_count")
                val periodicalCount: Int = 0,
                @SerializedName("goods_type")
                val goodsType: Int = 0,
                @SerializedName("marketing_info")
                val marketingInfo: MutableList<GoodsListItem>?= null,
                @SerializedName("img_url_compressed_larger")
                val imgUrlCompressedLarger: String = "")

data class MarketingInfo(@SerializedName("name")
                         val name: String = "")

data class RecentlyLearning(@SerializedName("msg")
                            val msg: String = "",
                            @SerializedName("code")
                            val code: Int = 0,
                            @SerializedName("data")
                            val data: Data?= null)



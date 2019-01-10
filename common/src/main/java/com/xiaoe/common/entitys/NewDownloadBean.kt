package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

data class ProductInfo(@SerializedName("summary")
                       val summary: String = "",
                       @SerializedName("img_url")
                       val imgUrl: String = "",
                       @SerializedName("goods_id")
                       val goodsId: String = "",
                       @SerializedName("periodical_count")
                       val periodicalCount: Int = 0,
                       @SerializedName("img_url_compressed")
                       val imgUrlCompressed: String = "",
                       @SerializedName("goods_type")
                       val goodsType: Int = 0,
                       @SerializedName("title")
                       val title: String = "",
                       @SerializedName("content")
                       val content: String = "",
                       @SerializedName("view_count")
                       val viewCount: Int = 0)


data class GoodsInfo(@SerializedName("summary")
                     val summary: String = "",
                     @SerializedName("img_url")
                     val imgUrl: String = "",
                     @SerializedName("goods_id")
                     val goodsId: String = "",
                     @SerializedName("periodical_count")
                     val periodicalCount: Int = 0,
                     @SerializedName("img_url_compressed")
                     val imgUrlCompressed: String = "",
                     @SerializedName("goods_type")
                     val goodsType: Int = 0,
                     @SerializedName("title")
                     val title: String = "",
                     @SerializedName("content")
                     val content: String = "",
                     @SerializedName("view_count")
                     val viewCount: Int = 0,
                     @SerializedName("download_count")
                     val downloadCount: Int = 0)


data class NewDownloadBean(@SerializedName("msg")
                           val msg: String = "",
                           @SerializedName("code")
                           val code: Int = 0,
                           @SerializedName("data")
                           val data: DownloadBeanData)


data class DownloadBeanData(@SerializedName("goods_info")
                            val goodsInfo: GoodsInfo,
                            @SerializedName("list")
                            val list: List<DownloadListItem>?)


data class DownloadListItem(@SerializedName("summary")
                            val summary: String = "",
                            @SerializedName("length")
                            val length: Int = 0,
                            @SerializedName("goods_id")
                            val goodsId: String = "",
                            @SerializedName("product_info")
                            val productInfo: ProductInfo,
                            @SerializedName("img_url_compressed")
                            val imgUrlCompressed: String = "",
                            @SerializedName("title")
                            val title: String = "",
                            @SerializedName("content")
                            val content: String = "",
                            @SerializedName("patch_img_url")
                            val patchImgUrl: String = "",
                            @SerializedName("img_url")
                            val imgUrl: String = "",
                            @SerializedName("download_url")
                            val downloadUrl: String = "",
                            @SerializedName("periodical_count")
                            val periodicalCount: Int = 0,
                            @SerializedName("goods_type")
                            val goodsType: Int = 0,
                            @SerializedName("view_count")
                            val viewCount: Int = 0,
                            @SerializedName("download_count")
                            val downloadCount: Int = 0)



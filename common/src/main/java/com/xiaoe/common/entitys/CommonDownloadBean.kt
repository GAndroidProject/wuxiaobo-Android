package com.xiaoe.common.entitys


import com.google.gson.annotations.SerializedName

/**
 * 下载列表专栏实体 bean
 *
 * @param isEnable      是否可选
 * @param isSelected    是否选中
 *
 * @author zak
 * @date 2019/1/3
 */
data class CommonDownloadBean(@SerializedName("img_url_compress")
                              var imgUrlCompress: String = "",
                              @SerializedName("audio_compress_url")
                              var audioCompressUrl: String = "",
                              @SerializedName("try_m3u8_url")
                              var tryM3U8Url: String = "",
                              @SerializedName("resource_type")
                              var resourceType: Int = 0,
                              @SerializedName("is_try")
                              var isTry: Int = 0,
                              @SerializedName("video_length")
                              var videoLength: Int = 0,
                              @SerializedName("title")
                              var title: String = "",
                              @SerializedName("try_audio_url")
                              var tryAudioUrl: String = "",
                              @SerializedName("video_url")
                              var videoUrl: String = "",
                              @SerializedName("img_url")
                              var imgUrl: String = "",
                              @SerializedName("audio_length")
                              var audioLength: Int = 0,
                              @SerializedName("resource_id")
                              var resourceId: String = "",
                              @SerializedName("audio_url")
                              var audioUrl: String = "",
                              @SerializedName("app_id")
                              var appId: String = "",
                              @SerializedName("m3u8_url")
                              var m3U8Url: String = "",
                              @SerializedName("start_at")
                              var startAt: String = "",
                              var isSelected: Boolean = false,
                              var isEnable: Boolean = true,
                              var periodicalCount: Int = 0, // 可下载资源数
                              var parentId: String = "",
                              var parentType: Int = 0,
                              var isLastItem: Boolean = false,
                              var topParentId: String = "",
                              var topParentType: Int = 0)
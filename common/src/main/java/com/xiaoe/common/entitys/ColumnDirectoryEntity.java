package com.xiaoe.common.entitys;

import java.io.Serializable;
import java.util.List;

public class ColumnDirectoryEntity implements Serializable {


    private List<ColumnSecondDirectoryEntity> resource_list;

    private boolean expand = false;// 是否展开
    private boolean isSelect = false; //是否选择
    /**
     * app_id : apppcHqlTPT3482
     * resource_id : a_5bc6df02009f9_lhQh0lbk
     * title : 测试音频-IOS
     * img_url : http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/204598cc9fd562b5cf31132baf85bd6c.jpg
     * img_url_compress :
     * start_at : 2018-10-17 21:05:30
     * try_m3u8_url :
     * try_audio_url :
     * resource_type : 2
     * video_length : 0
     * audio_length : 293
     * resource_list : []
     */

    private String app_id;
    private String resource_id;
    private String title;
    private String img_url;
    private String img_url_compress;
    private String start_at;
    private String try_m3u8_url;
    private String try_audio_url;
    private int resource_type;
    private int video_length;
    private int audio_length;
    private String audio_url;
    private String audio_compress_url;
    private String m3u8_url;

    private String bigColumnId;

    public List<ColumnSecondDirectoryEntity> getResource_list() {
        return resource_list;
    }

    public void setResource_list(List<ColumnSecondDirectoryEntity> resource_list) {
        this.resource_list = resource_list;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_url_compress() {
        return img_url_compress;
    }

    public void setImg_url_compress(String img_url_compress) {
        this.img_url_compress = img_url_compress;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getTry_m3u8_url() {
        return try_m3u8_url;
    }

    public void setTry_m3u8_url(String try_m3u8_url) {
        this.try_m3u8_url = try_m3u8_url;
    }

    public String getTry_audio_url() {
        return try_audio_url;
    }

    public void setTry_audio_url(String try_audio_url) {
        this.try_audio_url = try_audio_url;
    }

    public int getResource_type() {
        return resource_type;
    }

    public void setResource_type(int resource_type) {
        this.resource_type = resource_type;
    }

    public int getVideo_length() {
        return video_length;
    }

    public void setVideo_length(int video_length) {
        this.video_length = video_length;
    }

    public int getAudio_length() {
        return audio_length;
    }

    public void setAudio_length(int audio_length) {
        this.audio_length = audio_length;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getAudio_compress_url() {
        return audio_compress_url;
    }

    public void setAudio_compress_url(String audio_compress_url) {
        this.audio_compress_url = audio_compress_url;
    }

    public String getM3u8_url() {
        return m3u8_url;
    }

    public void setM3u8_url(String m3u8_url) {
        this.m3u8_url = m3u8_url;
    }

    public String getBigColumnId() {
        return bigColumnId;
    }

    public void setBigColumnId(String bigColumnId) {
        this.bigColumnId = bigColumnId;
    }
}

package com.xiaoe.common.entitys;

public class CommentEntity {

    /**
     * comment_id : 502536
     * user_id : u_591d643ce9c2c_fAbTq44T
     * content : 哄哄你
     * like_num : 0
     * src_comment_id : 0
     * src_content :
     * comment_at : 2018-10-30 16:11:20
     * is_admin : false
     * user_nickname : 日出日落
     * user_avatar : http://wechatavator-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/compress/u_591d643ce9c2c_fAbTq44T.png
     * src_wx_nickname :
     * src_wx_avatar :
     * is_praise : false
     */

    private int comment_id;
    private String user_id;
    private String content;
    private int like_num = 0;
    private int src_comment_id;
    private String src_content;
    private String comment_at;
    private boolean is_admin;
    private String user_nickname;
    private String user_avatar;
    private String src_wx_nickname;
    private String src_wx_avatar;
    private boolean is_praise = false;

    private boolean isDelete = false;

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getSrc_comment_id() {
        return src_comment_id;
    }

    public void setSrc_comment_id(int src_comment_id) {
        this.src_comment_id = src_comment_id;
    }

    public String getSrc_content() {
        return src_content;
    }

    public void setSrc_content(String src_content) {
        this.src_content = src_content;
    }

    public String getComment_at() {
        return comment_at;
    }

    public void setComment_at(String comment_at) {
        this.comment_at = comment_at;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getSrc_wx_nickname() {
        return src_wx_nickname;
    }

    public void setSrc_wx_nickname(String src_wx_nickname) {
        this.src_wx_nickname = src_wx_nickname;
    }

    public String getSrc_wx_avatar() {
        return src_wx_avatar;
    }

    public void setSrc_wx_avatar(String src_wx_avatar) {
        this.src_wx_avatar = src_wx_avatar;
    }

    public boolean isIs_praise() {
        return is_praise;
    }

    public void setIs_praise(boolean is_praise) {
        this.is_praise = is_praise;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}

package com.xiaoe.common.entitys;

import java.util.List;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessageEntity {
    /**
     * list : [{"id":7107,"app_id":"appiOW1KfWe9943","src_id":"","type":0,"user_id":"i_5be03f4a9b9b2_W08enCASgk","send_user_id":"","send_nick_name":"消息测试","source":0,"skip_type":0,"skip_target":"","title":"","content":"消息测试消息测试消息测试消息测试","content_clickable":"","send_at":"2018-11-13 21:50:45","state":0,"created_at":"2018-11-13 21:50:45","updated_at":"2018-11-13 21:50:45","wx_avatar":"","message_type":0,"time_from_now":"1天前"},{"id":4629,"app_id":"appiOW1KfWe9943","type":4,"record_id":"a_5bebc2c84ec66_sp8ZogYu","comment_id":"ea_5bec2efa3d653_BW2XCkb6","src_user_id":"i_5be03f4a9b9b2_W08enCASgk","src_content":"测试","user_id":"u_5b739f35e7ada_uh0dfhsqMr","praise_state":1,"wx_app_type":1,"created_at":"2018-11-14 22:20:00","updated_at":"2018-11-14 22:20:00","wx_nickname":"","wx_avatar":"","comment_src_type":"","comment_src_id":"","message_type":2,"time_from_now":"11小时前","skip_type":1,"skip_target":""},{"id":502675,"app_id":"appiOW1KfWe9943","user_id":"u_5beac201593bf_svN5l99uo4","type":1,"record_id":"a_5bebc2c84ec66_sp8ZogYu","record_title":"漂洋过海来看你","content":"http://appiow1kfwe9943.h5.inside.xiaoeknow.com/content_page/eyJ0eXBlIjoiMiIsInJlc291cmNlX3R5cGUiOjIsInJlc291cmNlX2lkIjoiYV81YmViYzJjODRlYzY2X3NwOFpvZ1l1IiwiYXBwX2lkIjoiYXBwaU9XMUtmV2U5OTQzIiwicHJvZHVjdF9pZCI6IiJ9","comment_state":0,"sub_comment_state":0,"src_comment_id":0,"src_user_id":"i_5be03f4a9b9b2_W08enCASgk","src_content":"看个锤子","is_admin":0,"admin_name":"","admin_content":"","admin_created_at":"","zan_num":0,"is_top":0,"wx_app_type":1,"is_exception":0,"exception_category":"","created_at":"2018-11-14 17:50:32","updated_at":"2018-11-14 17:50:32","wx_nickname":"Dzb","wx_avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/fc1qqUhicx1KBfvIBZKQghGNpibVUj2Wfoa4nibRlCoMgkEibbDDuUUV9bZC4Z8ESKibtZaXL41zGVJk5Mnm8laGkMg/132","message_type":1,"time_from_now":"15小时前","skip_type":2,"skip_target":"a_5bebc2c84ec66_sp8ZogYu"}]
     * message_last_id : 2018-11-13 21:50:45
     * comment_last_id : 502675
     * praise_last_id : 4629
     * unread_num : 3
     */

    private String message_last_id;
    private int comment_last_id;
    private int praise_last_id;
    private int unread_num;
    private List<ListBean> list;

    public String getMessage_last_id() {
        return message_last_id;
    }

    public void setMessage_last_id(String message_last_id) {
        this.message_last_id = message_last_id;
    }

    public int getComment_last_id() {
        return comment_last_id;
    }

    public void setComment_last_id(int comment_last_id) {
        this.comment_last_id = comment_last_id;
    }

    public int getPraise_last_id() {
        return praise_last_id;
    }

    public void setPraise_last_id(int praise_last_id) {
        this.praise_last_id = praise_last_id;
    }

    public int getUnread_num() {
        return unread_num;
    }

    public void setUnread_num(int unread_num) {
        this.unread_num = unread_num;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 7107
         * app_id : appiOW1KfWe9943
         * src_id :
         * type : 0
         * user_id : i_5be03f4a9b9b2_W08enCASgk
         * send_user_id :
         * send_nick_name : 消息测试
         * source : 0
         * skip_type : 0
         * sub_skip_type: 1
         * skip_target :
         * title :
         * content : 消息测试消息测试消息测试消息测试
         * content_clickable :
         * send_at : 2018-11-13 21:50:45
         * state : 0
         * created_at : 2018-11-13 21:50:45
         * updated_at : 2018-11-13 21:50:45
         * wx_avatar :
         * message_type : 0
         * time_from_now : 1天前
         * record_id : a_5bebc2c84ec66_sp8ZogYu
         * comment_id : ea_5bec2efa3d653_BW2XCkb6
         * src_user_id : i_5be03f4a9b9b2_W08enCASgk
         * src_content : 测试
         * praise_state : 1
         * wx_app_type : 1
         * wx_nickname :
         * comment_src_type :
         * comment_src_id :
         * record_title : 漂洋过海来看你
         * comment_state : 0
         * sub_comment_state : 0
         * src_comment_id : 0
         * is_admin : 0
         * admin_name :
         * admin_content :
         * admin_created_at :
         * zan_num : 0
         * is_top : 0
         * is_exception : 0
         * exception_category :
         */

        /**
         * 点赞、评论、系统消息id
         */
        private int id;
        /**
         * 应用Id（店铺ID）
         */
        private String app_id;
        /**
         * 回复消息对应的源反馈或者源评论
         */
        private String src_id;
        /**
         * 针对的类型，0-图文，1-音频，2-视频，3-社群，4-作业，5-打卡
         */
        private int type;
        /**
         * 用户ID。备注：type为2时user_id储存的是人群id
         */
        private String user_id;
        /**
         * 发送者用户ID
         */
        private String send_user_id;
        /**
         * 发送者用户昵称
         */
        private String send_nick_name;
        /**
         * 来源：0-系统管理员, 1-帖子回复， 2-系统管理员针对评论的回复，与src_id对应评论表,
         * 3-系统管理员针对用户反馈的回复，与src_id对应用户反馈表，4-直播间回复自动推送 5-打卡的被评论消息
         * 6-打卡的被回复消息 7-打卡的日记被点赞消息 8-打卡的被点评消息 9-打卡的奖励发放通知
         * 10-打卡的评论被点赞 11-打卡的补打卡提醒 12-打卡的作业补打卡提醒
         */
        private int source;
        /**
         * 跳转类型，0-不跳转， 1-图文，2-音频，3-视频，4-社群,5-跳转url, 6-专栏，7-直播间内，8-分答问题详情页 9-活动，21-电子书 16-打卡
         */
        private int skip_type;
        /**
         * 专栏类型，只有当 skip_type=6 时才有意义，默认0-无意义，1-普通专栏，2-大专栏，3-会员
         */
        private int sub_skip_type;
        /**
         * 跳转目的地
         */
        private String skip_target;
        private String title;
        private String content;
        /**
         * 可点击的文字
         */
        private String content_clickable;
        /**
         * 消息发送时间
         */
        private String send_at;
        /**
         * 状态 0-正常 1-被撤回
         */
        private int state;
        /**
         * 创建时间
         */
        private String created_at;
        /**
         * 更新时间，有修改自动更新
         */
        private String updated_at;
        /**
         * 用户头像
         */
        private String wx_avatar;
        /**
         * 消息类型：0-系统消息，1-评论消息，2-点赞消息
         */
        private int message_type;
        /**
         * 消息创建时间
         */
        private String time_from_now;
        /**
         * 评论所属记录id
         */
        private String record_id;
        /**
         * 对应评论id
         */
        private String comment_id;
        /**
         * 本评论对应的源用户id，如果src_comment_id为0或NULL时，说明是评论帖子，此字段存储发贴人的id,
         * 否则说明是对他人评论的回复，此字段存储源评论的用户id
         */
        private String src_user_id;
        /**
         * 本评论回复的源评论内容
         */
        private String src_content;
        /**
         * 点赞状态：0未点赞、1点赞
         */
        private int praise_state;
        /**
         * 数据来源 0-小程序 1-公众号
         */
        private int wx_app_type;
        /**
         * 微信昵称
         */
        private String wx_nickname;
        /**
         * 点赞的源评论类型
         */
        private String comment_src_type;
        /**
         * 点赞的源评论id
         */
        private String comment_src_id;
        /**
         * 评论所属记录名称（标题），帖子的话存截取的摘要
         */
        private String record_title;
        /**
         * 评论可见性（0-可见 1-隐藏 2-删除 3-屏蔽）
         */
        private int comment_state;
        /**
         * 评论可见性（0-可见 1-被关联隐藏）
         */
        private int sub_comment_state;
        /**
         * 本评论回复的源评论id
         */
        private int src_comment_id;
        /**
         * 是否管理员回复，0-不是 1-是 （—作废—）
         */
        private int is_admin;
        /**
         * 管理员名称
         */
        private String admin_name;
        /**
         * 管理员回复的内容
         */
        private String admin_content;
        /**
         * 管理员评论的时间
         */
        private String admin_created_at;
        /**
         * 点赞数
         */
        private int zan_num;
        /**
         * 是否置顶 0-不置顶 1-置顶
         */
        private int is_top;
        /**
         * 是否异常评论,0-正常,1-异常
         */
        private int is_exception;
        /**
         * 异常的分类
         */
        private String exception_category;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getSrc_id() {
            return src_id;
        }

        public void setSrc_id(String src_id) {
            this.src_id = src_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getSend_user_id() {
            return send_user_id;
        }

        public void setSend_user_id(String send_user_id) {
            this.send_user_id = send_user_id;
        }

        public String getSend_nick_name() {
            return send_nick_name;
        }

        public void setSend_nick_name(String send_nick_name) {
            this.send_nick_name = send_nick_name;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public int getSkip_type() {
            return skip_type;
        }

        public void setSkip_type(int skip_type) {
            this.skip_type = skip_type;
        }

        public int getSub_skip_type() {
            return sub_skip_type;
        }

        public void setSub_skip_type(int sub_skip_type) {
            this.sub_skip_type = sub_skip_type;
        }

        public String getSkip_target() {
            return skip_target;
        }

        public void setSkip_target(String skip_target) {
            this.skip_target = skip_target;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent_clickable() {
            return content_clickable;
        }

        public void setContent_clickable(String content_clickable) {
            this.content_clickable = content_clickable;
        }

        public String getSend_at() {
            return send_at;
        }

        public void setSend_at(String send_at) {
            this.send_at = send_at;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getWx_avatar() {
            return wx_avatar;
        }

        public void setWx_avatar(String wx_avatar) {
            this.wx_avatar = wx_avatar;
        }

        public int getMessage_type() {
            return message_type;
        }

        public void setMessage_type(int message_type) {
            this.message_type = message_type;
        }

        public String getTime_from_now() {
            return time_from_now;
        }

        public void setTime_from_now(String time_from_now) {
            this.time_from_now = time_from_now;
        }

        public String getRecord_id() {
            return record_id;
        }

        public void setRecord_id(String record_id) {
            this.record_id = record_id;
        }

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getSrc_user_id() {
            return src_user_id;
        }

        public void setSrc_user_id(String src_user_id) {
            this.src_user_id = src_user_id;
        }

        public String getSrc_content() {
            return src_content;
        }

        public void setSrc_content(String src_content) {
            this.src_content = src_content;
        }

        public int getPraise_state() {
            return praise_state;
        }

        public void setPraise_state(int praise_state) {
            this.praise_state = praise_state;
        }

        public int getWx_app_type() {
            return wx_app_type;
        }

        public void setWx_app_type(int wx_app_type) {
            this.wx_app_type = wx_app_type;
        }

        public String getWx_nickname() {
            return wx_nickname;
        }

        public void setWx_nickname(String wx_nickname) {
            this.wx_nickname = wx_nickname;
        }

        public String getComment_src_type() {
            return comment_src_type;
        }

        public void setComment_src_type(String comment_src_type) {
            this.comment_src_type = comment_src_type;
        }

        public String getComment_src_id() {
            return comment_src_id;
        }

        public void setComment_src_id(String comment_src_id) {
            this.comment_src_id = comment_src_id;
        }

        public String getRecord_title() {
            return record_title;
        }

        public void setRecord_title(String record_title) {
            this.record_title = record_title;
        }

        public int getComment_state() {
            return comment_state;
        }

        public void setComment_state(int comment_state) {
            this.comment_state = comment_state;
        }

        public int getSub_comment_state() {
            return sub_comment_state;
        }

        public void setSub_comment_state(int sub_comment_state) {
            this.sub_comment_state = sub_comment_state;
        }

        public int getSrc_comment_id() {
            return src_comment_id;
        }

        public void setSrc_comment_id(int src_comment_id) {
            this.src_comment_id = src_comment_id;
        }

        public int getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(int is_admin) {
            this.is_admin = is_admin;
        }

        public String getAdmin_name() {
            return admin_name;
        }

        public void setAdmin_name(String admin_name) {
            this.admin_name = admin_name;
        }

        public String getAdmin_content() {
            return admin_content;
        }

        public void setAdmin_content(String admin_content) {
            this.admin_content = admin_content;
        }

        public String getAdmin_created_at() {
            return admin_created_at;
        }

        public void setAdmin_created_at(String admin_created_at) {
            this.admin_created_at = admin_created_at;
        }

        public int getZan_num() {
            return zan_num;
        }

        public void setZan_num(int zan_num) {
            this.zan_num = zan_num;
        }

        public int getIs_top() {
            return is_top;
        }

        public void setIs_top(int is_top) {
            this.is_top = is_top;
        }

        public int getIs_exception() {
            return is_exception;
        }

        public void setIs_exception(int is_exception) {
            this.is_exception = is_exception;
        }

        public String getException_category() {
            return exception_category;
        }

        public void setException_category(String exception_category) {
            this.exception_category = exception_category;
        }
    }

}

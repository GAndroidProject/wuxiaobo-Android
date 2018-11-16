package com.xiaoe.common.entitys;

/**
 * @author flynnWang
 * @date 2018/11/14
 * <p>
 * 描述：
 */
public class HistoryMessageReq {

    /**
     * 参数名	必选	类型	说明	备注（示例）
     * app_id	是	string	app_id	appxxx
     * user_id	是	string	用户id	i_5bcxxx
     * buz_data	是	array	其他参数	`
     * buz_data参数：
     * <p>
     * 参数名	必选	类型	说明	备注（示例）
     * page_size	否	number	默认20	30
     * message_last_id	是	string	最后一条消息时间（没有传-1）	2018-11-01 10:17:06
     * comment_last_id	是	string	最后一条评论时间（没有传-1）	2018-11-01 10:17:06
     * praise_last_id	是	string	最后一条点赞时间（没有传-1）	2018-11-01 10:17:06
     */
    private String app_id;
    private String user_id;
    private ListBean buz_data;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ListBean getBuz_data() {
        return buz_data;
    }

    public void setBuz_data(ListBean buz_data) {
        this.buz_data = buz_data;
    }

    public static class ListBean {
        private String page_size;
        private String message_last_id;
        private String comment_last_id;
        private String praise_last_id;

        public String getPage_size() {
            return page_size;
        }

        public void setPage_size(String page_size) {
            this.page_size = page_size;
        }

        public String getMessage_last_id() {
            return message_last_id;
        }

        public void setMessage_last_id(String message_last_id) {
            this.message_last_id = message_last_id;
        }

        public String getComment_last_id() {
            return comment_last_id;
        }

        public void setComment_last_id(String comment_last_id) {
            this.comment_last_id = comment_last_id;
        }

        public String getPraise_last_id() {
            return praise_last_id;
        }

        public void setPraise_last_id(String praise_last_id) {
            this.praise_last_id = praise_last_id;
        }
    }
}

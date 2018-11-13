package xiaoe.com.common.entitys;

import java.util.List;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessageEntity {

    /**
     * list : [{"id":6811,"app_id":"appZel1uGIB1234","src_id":"d_5be24a2b6dc18_IEV4CTcK","type":0,"user_id":"u_5bd2dd6f7cdeb_7CJ9wplRy1","send_user_id":"u_5bd2f811385b7_bP0IlYJSSK","send_nick_name":"芒果酱","source":5,"skip_type":16,"skip_target":"/xiaoe_clock/calendar_clock/ac_5bdd05eb6fac1_hRIditP6#/diaryDetail?diary_id=d_5be24a2b6dc18_IEV4CTcK","title":"","content":"哈哈还是寄到你家","content_clickable":"","send_at":"2018-11-07 10:17:23","state":0,"created_at":"2018-11-07 10:17:23","updated_at":"2018-11-07 10:17:23","wx_avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoRPPYK1qvPib2WuicJ1qnjpzkR0pMYeW6MYDFicCw7gL5ZEqFohT9Goj7UV0ODWQzALQvcjQXrFDAog/132","message_type":0,"time_from_now":"6天前"}]
     * message_last_id : 2018-11-07 10:17:06
     * unread_num : 2
     */

    private String message_last_id;
    private int unread_num;
    private List<ListBean> list;

    public String getMessage_last_id() {
        return message_last_id;
    }

    public void setMessage_last_id(String message_last_id) {
        this.message_last_id = message_last_id;
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
         * id : 6811
         * app_id : appZel1uGIB1234
         * src_id : d_5be24a2b6dc18_IEV4CTcK
         * type : 0
         * user_id : u_5bd2dd6f7cdeb_7CJ9wplRy1
         * send_user_id : u_5bd2f811385b7_bP0IlYJSSK
         * send_nick_name : 芒果酱
         * source : 5
         * skip_type : 16
         * skip_target : /xiaoe_clock/calendar_clock/ac_5bdd05eb6fac1_hRIditP6#/diaryDetail?diary_id=d_5be24a2b6dc18_IEV4CTcK
         * title :
         * content : 哈哈还是寄到你家
         * content_clickable :
         * send_at : 2018-11-07 10:17:23
         * state : 0
         * created_at : 2018-11-07 10:17:23
         * updated_at : 2018-11-07 10:17:23
         * wx_avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoRPPYK1qvPib2WuicJ1qnjpzkR0pMYeW6MYDFicCw7gL5ZEqFohT9Goj7UV0ODWQzALQvcjQXrFDAog/132
         * message_type : 0
         * time_from_now : 6天前
         */

        private int id;
        private String app_id;
        private String src_id;
        private int type;
        private String user_id;
        private String send_user_id;
        private String send_nick_name;
        private int source;
        private int skip_type;
        private String skip_target;
        private String title;
        private String content;
        private String content_clickable;
        private String send_at;
        private int state;
        private String created_at;
        private String updated_at;
        private String wx_avatar;
        private int message_type;
        private String time_from_now;

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
    }
}

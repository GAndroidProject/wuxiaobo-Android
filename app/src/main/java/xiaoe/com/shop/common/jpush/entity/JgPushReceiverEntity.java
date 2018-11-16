package xiaoe.com.shop.common.jpush.entity;

/**
 * @author flynnWang
 * @date 2018/11/15
 * <p>
 * 描述：
 */
public class JgPushReceiverEntity {

    /**
     * 跳转类型：0-无跳转，1-图文，2-音频，3-视频，5-外部链接，6-专栏，7-直播
     */
    private int action;
    private ActionParams action_params;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public ActionParams getAction_params() {
        return action_params;
    }

    public void setAction_params(ActionParams action_params) {
        this.action_params = action_params;
    }

    public static class ActionParams {
        /**
         * 1-图文，2-音频，3-视频，4-直播，5-会员，6-专栏，8-大专栏，20-电子书，21-实物商品
         */
        private String resource_type;
        private String resource_id;
        private String website_url;

        public String getResource_type() {
            return resource_type;
        }

        public void setResource_type(String resource_type) {
            this.resource_type = resource_type;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public String getWebsite_url() {
            return website_url;
        }

        public void setWebsite_url(String website_url) {
            this.website_url = website_url;
        }

        @Override
        public String toString() {
            return "ActionParams{" +
                    "resource_type='" + resource_type + '\'' +
                    ", resource_id='" + resource_id + '\'' +
                    ", website_url='" + website_url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JgPushReceiverEntity{" +
                "action=" + action +
                ", action_params=" + action_params +
                '}';
    }
}

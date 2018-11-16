package com.xiaoe.common.entitys;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 音频商品详情
 */
public class AudioGoodsDetail {

    /**
     * distribute_info : {"distribute_share_url":"openapi/server","distribute_url":"openapi/server/inviteCard/eyJwYXltZW50X3R5cGUiOjMsInJlc291cmNlX3R5cGUiOjIsInJlc291cmNlX2lkIjoiYV81YmM3NDVlNTU2NGUzX3RWSGx0TldIIiwicHJvZHVjdF9pZCI6InBfNWJjODc1YjAxNDc3NV9Ua1dYdEU5eSJ9","price":0}
     * marketing_activity : {"coupon_list":[{"bind_res_count":0,"id":"cou_5aab753a96930-VNojXS","invalid_at":"2018-12-31 23:55:00","price":1,"require_price":0,"title":"测试用","type":1,"valid_at":"2018-03-16 15:40:00"},{"bind_res_count":0,"id":"cou_5aab753a96930-VNojXW","invalid_at":"2018-12-31 23:55:00","price":1,"require_price":0,"title":"测试用","type":1,"valid_at":"2018-03-16 15:40:00"},{"bind_res_count":0,"id":"cou_5aacc946ddcf5-4CW1yt","invalid_at":"2018-12-29 19:55:00","price":1,"require_price":0,"title":"neo1","type":1,"valid_at":"2018-03-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bb0bac415869-JhJsuP","invalid_at":"2018-12-01 00:00:00","price":200,"require_price":0,"title":"测试用户领取","type":1,"valid_at":"2018-09-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bc9c8f12c95c-rLl8mX","invalid_at":"2018-10-31 20:30:00","price":2000,"require_price":3000,"title":"大于","type":1,"valid_at":"2018-10-02 23:50:00"},{"bind_res_count":0,"id":"cou_5bc71e5f745cb-V1pvzk","invalid_at":"2018-10-31 19:30:00","price":222200,"require_price":0,"title":"1212121","type":1,"valid_at":"2018-10-17 19:30:00"},{"bind_res_count":0,"id":"cou_5bb2044d796c9-z0HYax","invalid_at":"2018-10-30 19:00:00","price":1200,"require_price":0,"title":"测试店铺用户领取","type":1,"valid_at":"2018-10-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bc9ce915b05a-aarklW","invalid_at":"2018-10-30 17:45:00","price":2000,"require_price":3000,"title":"大于测试","type":1,"valid_at":"2018-10-02 08:30:00"},{"bind_res_count":0,"id":"cou_5bbf34aa5ef86-bHukaD","invalid_at":"2018-10-30 00:00:00","price":3300,"require_price":0,"title":"用户领取","type":1,"valid_at":"2018-10-03 00:00:00"},{"bind_res_count":0,"id":"cou_5bca934576d98-xPsU4x","invalid_at":"2018-10-25 18:50:00","price":100,"require_price":0,"title":"1111","type":1,"valid_at":"2018-10-19 10:10:00"},{"bind_res_count":0,"id":"cou_5bcc3e0c9905f-ZciIYm","invalid_at":"2018-10-24 16:50:00","price":1000,"require_price":0,"title":"福利。。。。","type":1,"valid_at":"2018-10-20 16:50:00"}],"information_data":{"form_content":"","form_id":"","switch":0},"marketing_info":[],"marketing_info_sort":[]}
     * product_info : {"product_list":[{"app_id":"apppcHqlTPT3482","id":"p_5bc875b014775_TkWXtE9y","img_url":"http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/8191f5460ebc74531d32fd874a9f5e5d.jpg","is_member":0,"member_type":1,"purchase_count":1,"resource_count":1,"title":"搞笑表情包1958","update_num":1,"url":"open/content_page/eyJ0eXBlIjozLCJyZXNvdXJjZV90eXBlIjo2LCJyZXNvdXJjZV9pZCI6IiIsInByb2R1Y3RfaWQiOiJwXzViYzg3NWIwMTQ3NzVfVGtXWHRFOXkiLCJhcHBfaWQiOiJhcHBwY0hxbFRQVDM0ODIifQ"},{"app_id":"apppcHqlTPT3482","id":"p_5bcd96f64e15d_xcdy7xCO","img_url":"http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/a426b9415a4af5f423bb8dafd1017658.jpg","is_member":0,"member_type":1,"purchase_count":0,"resource_count":3,"title":"吴晓波最近更新的音频列表专栏","update_num":3,"url":"open/content_page/eyJ0eXBlIjozLCJyZXNvdXJjZV90eXBlIjo2LCJyZXNvdXJjZV9pZCI6IiIsInByb2R1Y3RfaWQiOiJwXzViY2Q5NmY2NGUxNWRfeGNkeTd4Q08iLCJhcHBfaWQiOiJhcHBwY0hxbFRQVDM0ODIifQ"}]}
     * resource_info : {"content":"<p>哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈哈哈<img src=\"http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/ueditor/78670000_1539786162.gif\" title=\".gif\" alt=\"704832c1-b369-4f41-a5af-f1ac2fcc0f3a.gif\"/><\/p>","has_buy":1,"has_stock":false,"img_url_compressed":"http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/compress/160120c918ce935130ed7401ad236b93f5cf51.jpg","is_can_buy":1,"is_stop_sell":0,"is_try":0,"line_price":"","need_user_info":0,"payment_type":3,"price":0,"product_id":"","purchase_count":0,"recycle_bin_state":0,"resource_id":"a_5bc745e5564e3_tVHltNWH","resource_type":2,"stock":"","summary":"","time_left":-420608,"title":"音频-ST","preview_content":"","preview_audio_url":"","preview_audio_m3u8_url":""}
     * send_friend : {"discount":{"discount_info":"","is_discount":""},"is_show":0}
     */

    private DistributeInfoBean distribute_info;
    private MarketingActivityBean marketing_activity;
    private ProductInfoBean product_info;
    private ResourceInfoBean resource_info;
    private SendFriendBean send_friend;

    public DistributeInfoBean getDistribute_info() {
        return distribute_info;
    }

    public void setDistribute_info(DistributeInfoBean distribute_info) {
        this.distribute_info = distribute_info;
    }

    public MarketingActivityBean getMarketing_activity() {
        return marketing_activity;
    }

    public void setMarketing_activity(MarketingActivityBean marketing_activity) {
        this.marketing_activity = marketing_activity;
    }

    public ProductInfoBean getProduct_info() {
        return product_info;
    }

    public void setProduct_info(ProductInfoBean product_info) {
        this.product_info = product_info;
    }

    public ResourceInfoBean getResource_info() {
        return resource_info;
    }

    public void setResource_info(ResourceInfoBean resource_info) {
        this.resource_info = resource_info;
    }

    public SendFriendBean getSend_friend() {
        return send_friend;
    }

    public void setSend_friend(SendFriendBean send_friend) {
        this.send_friend = send_friend;
    }

    public static class DistributeInfoBean {
        /**
         * distribute_share_url : openapi/server
         * distribute_url : openapi/server/inviteCard/eyJwYXltZW50X3R5cGUiOjMsInJlc291cmNlX3R5cGUiOjIsInJlc291cmNlX2lkIjoiYV81YmM3NDVlNTU2NGUzX3RWSGx0TldIIiwicHJvZHVjdF9pZCI6InBfNWJjODc1YjAxNDc3NV9Ua1dYdEU5eSJ9
         * price : 0
         */

        private String distribute_share_url;
        private String distribute_url;
        private int price;

        public String getDistribute_share_url() {
            return distribute_share_url;
        }

        public void setDistribute_share_url(String distribute_share_url) {
            this.distribute_share_url = distribute_share_url;
        }

        public String getDistribute_url() {
            return distribute_url;
        }

        public void setDistribute_url(String distribute_url) {
            this.distribute_url = distribute_url;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }

    public static class MarketingActivityBean {
        /**
         * coupon_list : [{"bind_res_count":0,"id":"cou_5aab753a96930-VNojXS","invalid_at":"2018-12-31 23:55:00","price":1,"require_price":0,"title":"测试用","type":1,"valid_at":"2018-03-16 15:40:00"},{"bind_res_count":0,"id":"cou_5aab753a96930-VNojXW","invalid_at":"2018-12-31 23:55:00","price":1,"require_price":0,"title":"测试用","type":1,"valid_at":"2018-03-16 15:40:00"},{"bind_res_count":0,"id":"cou_5aacc946ddcf5-4CW1yt","invalid_at":"2018-12-29 19:55:00","price":1,"require_price":0,"title":"neo1","type":1,"valid_at":"2018-03-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bb0bac415869-JhJsuP","invalid_at":"2018-12-01 00:00:00","price":200,"require_price":0,"title":"测试用户领取","type":1,"valid_at":"2018-09-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bc9c8f12c95c-rLl8mX","invalid_at":"2018-10-31 20:30:00","price":2000,"require_price":3000,"title":"大于","type":1,"valid_at":"2018-10-02 23:50:00"},{"bind_res_count":0,"id":"cou_5bc71e5f745cb-V1pvzk","invalid_at":"2018-10-31 19:30:00","price":222200,"require_price":0,"title":"1212121","type":1,"valid_at":"2018-10-17 19:30:00"},{"bind_res_count":0,"id":"cou_5bb2044d796c9-z0HYax","invalid_at":"2018-10-30 19:00:00","price":1200,"require_price":0,"title":"测试店铺用户领取","type":1,"valid_at":"2018-10-01 00:00:00"},{"bind_res_count":0,"id":"cou_5bc9ce915b05a-aarklW","invalid_at":"2018-10-30 17:45:00","price":2000,"require_price":3000,"title":"大于测试","type":1,"valid_at":"2018-10-02 08:30:00"},{"bind_res_count":0,"id":"cou_5bbf34aa5ef86-bHukaD","invalid_at":"2018-10-30 00:00:00","price":3300,"require_price":0,"title":"用户领取","type":1,"valid_at":"2018-10-03 00:00:00"},{"bind_res_count":0,"id":"cou_5bca934576d98-xPsU4x","invalid_at":"2018-10-25 18:50:00","price":100,"require_price":0,"title":"1111","type":1,"valid_at":"2018-10-19 10:10:00"},{"bind_res_count":0,"id":"cou_5bcc3e0c9905f-ZciIYm","invalid_at":"2018-10-24 16:50:00","price":1000,"require_price":0,"title":"福利。。。。","type":1,"valid_at":"2018-10-20 16:50:00"}]
         * information_data : {"form_content":"","form_id":"","switch":0}
         * marketing_info : []
         * marketing_info_sort : []
         */

        private InformationDataBean information_data;
        private List<CouponListBean> coupon_list;
        private List<?> marketing_info;
        private List<?> marketing_info_sort;

        public InformationDataBean getInformation_data() {
            return information_data;
        }

        public void setInformation_data(InformationDataBean information_data) {
            this.information_data = information_data;
        }

        public List<CouponListBean> getCoupon_list() {
            return coupon_list;
        }

        public void setCoupon_list(List<CouponListBean> coupon_list) {
            this.coupon_list = coupon_list;
        }

        public List<?> getMarketing_info() {
            return marketing_info;
        }

        public void setMarketing_info(List<?> marketing_info) {
            this.marketing_info = marketing_info;
        }

        public List<?> getMarketing_info_sort() {
            return marketing_info_sort;
        }

        public void setMarketing_info_sort(List<?> marketing_info_sort) {
            this.marketing_info_sort = marketing_info_sort;
        }

        public static class InformationDataBean {
            /**
             * form_content :
             * form_id :
             * switch : 0
             */

            private String form_content;
            private String form_id;
            @SerializedName("switch")
            private int switchX;

            public String getForm_content() {
                return form_content;
            }

            public void setForm_content(String form_content) {
                this.form_content = form_content;
            }

            public String getForm_id() {
                return form_id;
            }

            public void setForm_id(String form_id) {
                this.form_id = form_id;
            }

            public int getSwitchX() {
                return switchX;
            }

            public void setSwitchX(int switchX) {
                this.switchX = switchX;
            }
        }

        public static class CouponListBean {
            /**
             * bind_res_count : 0
             * id : cou_5aab753a96930-VNojXS
             * invalid_at : 2018-12-31 23:55:00
             * price : 1
             * require_price : 0
             * title : 测试用
             * type : 1
             * valid_at : 2018-03-16 15:40:00
             */

            private int bind_res_count;
            private String id;
            private String invalid_at;
            private int price;
            private int require_price;
            private String title;
            private int type;
            private String valid_at;

            public int getBind_res_count() {
                return bind_res_count;
            }

            public void setBind_res_count(int bind_res_count) {
                this.bind_res_count = bind_res_count;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getInvalid_at() {
                return invalid_at;
            }

            public void setInvalid_at(String invalid_at) {
                this.invalid_at = invalid_at;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public int getRequire_price() {
                return require_price;
            }

            public void setRequire_price(int require_price) {
                this.require_price = require_price;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getValid_at() {
                return valid_at;
            }

            public void setValid_at(String valid_at) {
                this.valid_at = valid_at;
            }
        }
    }

    public static class ProductInfoBean {
        private List<ProductListBean> product_list;

        public List<ProductListBean> getProduct_list() {
            return product_list;
        }

        public void setProduct_list(List<ProductListBean> product_list) {
            this.product_list = product_list;
        }

        public static class ProductListBean {
            /**
             * app_id : apppcHqlTPT3482
             * id : p_5bc875b014775_TkWXtE9y
             * img_url : http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/8191f5460ebc74531d32fd874a9f5e5d.jpg
             * is_member : 0
             * member_type : 1
             * purchase_count : 1
             * resource_count : 1
             * title : 搞笑表情包1958
             * update_num : 1
             * url : open/content_page/eyJ0eXBlIjozLCJyZXNvdXJjZV90eXBlIjo2LCJyZXNvdXJjZV9pZCI6IiIsInByb2R1Y3RfaWQiOiJwXzViYzg3NWIwMTQ3NzVfVGtXWHRFOXkiLCJhcHBfaWQiOiJhcHBwY0hxbFRQVDM0ODIifQ
             */

            private String app_id;
            private String id;
            private String img_url;
            private int is_member;
            private int member_type;
            private int purchase_count;
            private int resource_count;
            private String title;
            private int update_num;
            private String url;

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }

            public int getIs_member() {
                return is_member;
            }

            public void setIs_member(int is_member) {
                this.is_member = is_member;
            }

            public int getMember_type() {
                return member_type;
            }

            public void setMember_type(int member_type) {
                this.member_type = member_type;
            }

            public int getPurchase_count() {
                return purchase_count;
            }

            public void setPurchase_count(int purchase_count) {
                this.purchase_count = purchase_count;
            }

            public int getResource_count() {
                return resource_count;
            }

            public void setResource_count(int resource_count) {
                this.resource_count = resource_count;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getUpdate_num() {
                return update_num;
            }

            public void setUpdate_num(int update_num) {
                this.update_num = update_num;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    public static class ResourceInfoBean {
        /**
         * content : <p>哈哈哈啊哈哈哈哈哈哈哈哈哈哈哈哈哈哈<img src="http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/ueditor/78670000_1539786162.gif" title=".gif" alt="704832c1-b369-4f41-a5af-f1ac2fcc0f3a.gif"/></p>
         * has_buy : 1
         * has_stock : false
         * img_url_compressed : http://wechatapppro-1252524126.file.myqcloud.com/apppcHqlTPT3482/image/compress/160120c918ce935130ed7401ad236b93f5cf51.jpg
         * is_can_buy : 1
         * is_stop_sell : 0
         * is_try : 0
         * line_price :
         * need_user_info : 0
         * payment_type : 3
         * price : 0
         * product_id :
         * purchase_count : 0
         * recycle_bin_state : 0
         * resource_id : a_5bc745e5564e3_tVHltNWH
         * resource_type : 2
         * stock :
         * summary :
         * time_left : -420608
         * title : 音频-ST
         * preview_content :
         * preview_audio_url :
         * preview_audio_m3u8_url :
         */

        private String content;
        private int has_buy;
        private boolean has_stock;
        private String img_url_compressed;
        private int is_can_buy;
        private int is_stop_sell;
        private int is_try;
        private String line_price;
        private int need_user_info;
        private int payment_type;
        private int price;
        private String product_id;
        private int purchase_count;
        private int recycle_bin_state;
        private String resource_id;
        private int resource_type;
        private String stock;
        private String summary;
        private int time_left;
        private String title;
        private String preview_content;
        private String preview_audio_url;
        private String preview_audio_m3u8_url;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getHas_buy() {
            return has_buy;
        }

        public void setHas_buy(int has_buy) {
            this.has_buy = has_buy;
        }

        public boolean isHas_stock() {
            return has_stock;
        }

        public void setHas_stock(boolean has_stock) {
            this.has_stock = has_stock;
        }

        public String getImg_url_compressed() {
            return img_url_compressed;
        }

        public void setImg_url_compressed(String img_url_compressed) {
            this.img_url_compressed = img_url_compressed;
        }

        public int getIs_can_buy() {
            return is_can_buy;
        }

        public void setIs_can_buy(int is_can_buy) {
            this.is_can_buy = is_can_buy;
        }

        public int getIs_stop_sell() {
            return is_stop_sell;
        }

        public void setIs_stop_sell(int is_stop_sell) {
            this.is_stop_sell = is_stop_sell;
        }

        public int getIs_try() {
            return is_try;
        }

        public void setIs_try(int is_try) {
            this.is_try = is_try;
        }

        public String getLine_price() {
            return line_price;
        }

        public void setLine_price(String line_price) {
            this.line_price = line_price;
        }

        public int getNeed_user_info() {
            return need_user_info;
        }

        public void setNeed_user_info(int need_user_info) {
            this.need_user_info = need_user_info;
        }

        public int getPayment_type() {
            return payment_type;
        }

        public void setPayment_type(int payment_type) {
            this.payment_type = payment_type;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public int getPurchase_count() {
            return purchase_count;
        }

        public void setPurchase_count(int purchase_count) {
            this.purchase_count = purchase_count;
        }

        public int getRecycle_bin_state() {
            return recycle_bin_state;
        }

        public void setRecycle_bin_state(int recycle_bin_state) {
            this.recycle_bin_state = recycle_bin_state;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public int getResource_type() {
            return resource_type;
        }

        public void setResource_type(int resource_type) {
            this.resource_type = resource_type;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public int getTime_left() {
            return time_left;
        }

        public void setTime_left(int time_left) {
            this.time_left = time_left;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPreview_content() {
            return preview_content;
        }

        public void setPreview_content(String preview_content) {
            this.preview_content = preview_content;
        }

        public String getPreview_audio_url() {
            return preview_audio_url;
        }

        public void setPreview_audio_url(String preview_audio_url) {
            this.preview_audio_url = preview_audio_url;
        }

        public String getPreview_audio_m3u8_url() {
            return preview_audio_m3u8_url;
        }

        public void setPreview_audio_m3u8_url(String preview_audio_m3u8_url) {
            this.preview_audio_m3u8_url = preview_audio_m3u8_url;
        }
    }

    public static class SendFriendBean {
        /**
         * discount : {"discount_info":"","is_discount":""}
         * is_show : 0
         */

        private DiscountBean discount;
        private int is_show;

        public DiscountBean getDiscount() {
            return discount;
        }

        public void setDiscount(DiscountBean discount) {
            this.discount = discount;
        }

        public int getIs_show() {
            return is_show;
        }

        public void setIs_show(int is_show) {
            this.is_show = is_show;
        }

        public static class DiscountBean {
            /**
             * discount_info :
             * is_discount :
             */

            private String discount_info;
            private String is_discount;

            public String getDiscount_info() {
                return discount_info;
            }

            public void setDiscount_info(String discount_info) {
                this.discount_info = discount_info;
            }

            public String getIs_discount() {
                return is_discount;
            }

            public void setIs_discount(String is_discount) {
                this.is_discount = is_discount;
            }
        }
    }
}

package com.xiaoe.common.entitys;

import java.util.List;

/**
 * @author yangchao
 * @date 2018/11/7
 * <p>
 * 描述：
 */
public class ExchangeSuccessInfo {

    /**
     * redeem_code : Xj4fVuVC
     * goods_data : [{"resource_type":5,"resource_id":"p_5be1850c9a271_kkddGSN6"}]
     * coupon_data : [{"id":"cu_5be2cf972176d_wbVkrvS0","app_id":"apppcHqlTPT3482","cou_id":"cou_5be2a2dc372ff-NPp36d","user_id":"u_5bc5884fa85f2_JKWDqr4980","get_type":2,"receive_at":"2018-11-07 19:42:15","invalid_at":"2018-11-30 12:00:00"}]
     * activity_id : ma_rd_5be2cf3a81d9b_zmN6ture
     */
    public Data data;
    private int code;
    private String msg;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public static class Data{
        private String redeem_code;
        private String activity_id;
        private List<GoodsDataBean> goods_data;
        private List<CouponDataBean> coupon_data;

        public String getRedeem_code() {
            return redeem_code;
        }

        public void setRedeem_code(String redeem_code) {
            this.redeem_code = redeem_code;
        }

        public String getActivity_id() {
            return activity_id;
        }

        public void setActivity_id(String activity_id) {
            this.activity_id = activity_id;
        }

        public List<GoodsDataBean> getGoods_data() {
            return goods_data;
        }

        public void setGoods_data(List<GoodsDataBean> goods_data) {
            this.goods_data = goods_data;
        }

        public List<CouponDataBean> getCoupon_data() {
            return coupon_data;
        }

        public void setCoupon_data(List<CouponDataBean> coupon_data) {
            this.coupon_data = coupon_data;
        }
    }

    public static class GoodsDataBean {
        /**
         * resource_type : 5
         * resource_id : p_5be1850c9a271_kkddGSN6
         */

        // 【注意5,6类型  5-专栏 6-会员  23-超级会员】
        private int resource_type;
        private String resource_id;

        public int getResource_type() {
            return resource_type;
        }

        public void setResource_type(int resource_type) {
            this.resource_type = resource_type;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }
    }

    public static class CouponDataBean {
        /**
         * id : cu_5be2cf972176d_wbVkrvS0
         * app_id : apppcHqlTPT3482
         * cou_id : cou_5be2a2dc372ff-NPp36d
         * user_id : u_5bc5884fa85f2_JKWDqr4980
         * get_type : 2
         * receive_at : 2018-11-07 19:42:15
         * invalid_at : 2018-11-30 12:00:00
         */

        private String id;
        private String app_id;
        private String cou_id;
        private String user_id;
        private int get_type;
        private String receive_at;
        private String invalid_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getCou_id() {
            return cou_id;
        }

        public void setCou_id(String cou_id) {
            this.cou_id = cou_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getGet_type() {
            return get_type;
        }

        public void setGet_type(int get_type) {
            this.get_type = get_type;
        }

        public String getReceive_at() {
            return receive_at;
        }

        public void setReceive_at(String receive_at) {
            this.receive_at = receive_at;
        }

        public String getInvalid_at() {
            return invalid_at;
        }

        public void setInvalid_at(String invalid_at) {
            this.invalid_at = invalid_at;
        }
    }
}

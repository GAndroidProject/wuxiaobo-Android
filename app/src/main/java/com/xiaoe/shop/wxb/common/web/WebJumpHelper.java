package com.xiaoe.shop.wxb.common.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.ToastUtils;

/**
 * @author flynnWang
 * @date 2018/12/14
 * <p>
 * 描述：
 */
public class WebJumpHelper {

    private static final String TAG = "WebJumpHelper";

    private Context mContext;

    /**
     * 小鹅通域名(h5)
     */
    public static final String[] XIAOE_DOMAIN = new String[]{"h5.xiaoeknow.com", "h5.xeknow.com", "h5.xiaoe-tech.com",
            "h5.inside.xiaoeknow.com", "h5.inside.xeknow.com", "h5.inside.xiaoe-tech.com"};

    public enum UrlType {
        /**
         * 微信中打开
         */
        WECHAT_OPEN,
        /**
         * 应用内跳转页面
         */
        APP_JUMP_OPEN,
        /**
         * 直接打开
         */
        DIRECTLY_OPEN,
        /**
         * 浏览器打开
         */
        BROWSER_OPEN
    }

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final WebJumpHelper INSTANCE = new WebJumpHelper();
    }

    public static WebJumpHelper getInstance() {
        return Holder.INSTANCE;
    }

    private WebJumpHelper() {
    }

    /**
     * 获取访问url的类型
     * <p>
     * 小鹅通域名：
     * {app_id}.h5.xiaoeknow.com
     * {app_id}.h5.xeknow.com
     * {app_id}.h5.xiaoe-tech.com
     * {app_id}.h5.inside.xiaoeknow.com
     * {app_id}.h5.inside.xeknow.com
     * {app_id}.h5.inside.xiaoe-tech.com
     * <p>
     * 1、非上述域名的，直接打开
     * <p>
     * 2、如果是上述域名，再识别是否是/content_page/打头的接口
     * 是 content_page 直接解析base64
     * <p>
     * 3、非 content_page 提示复制链接微信打开
     * <p>
     * 例子：https://wxff1d6eb74a78f53e.h5.inside.xiaoe-tech.com
     * /content_page
     * /eyJ0eXBlIjozLCJyZXNvdXJjZV90eXBlIjoiIiwicmVzb3VyY2VfaWQiOiIiLCJwcm9kdWN0X2lkIjoicF81YzEzNGNlYTFlYWU0X0YzaW9RcjhWIiwiYXBwX2lkIjoiYXBwaU9XMUtmV2U5OTQzIn0
     *
     * @param url
     * @return
     */
    public UrlType getUrlType(@NonNull Context context, @NonNull String url) {
        Log.d(TAG, "getUrlType: url " + url);

        mContext = context;

        if (isXiaoeDomain(url)) {
            String contentPage = "/content_page/";

            if (url.contains(contentPage)) {
                int index = url.indexOf(contentPage);
                String base64 = url.substring(index + contentPage.length());
                byte[] base64Array = Base64.decode(base64, Base64.DEFAULT);

                String jsonString = new String(base64Array);
                ContentPageBean contentPageBean = JSON.parseObject(jsonString, ContentPageBean.class);

//                Log.d(TAG, "getUrlType: \nbase64-> " + base64 + "\njsonString " + jsonString);
                Log.d(TAG, "getUrlType: " + contentPageBean);

                return jumpDetail(contentPageBean);
            } else {
                return UrlType.WECHAT_OPEN;
            }
        }

        return UrlType.DIRECTLY_OPEN;
    }

    private UrlType jumpDetail(ContentPageBean contentPageBean) {
        /*
         * type=2时：resource_type：1-图文，2-音频，3-视频，4-直播，5-报名，7-社群，16-小鹅打卡，20-电子书，21-实物商品
         * type=3时，product_id 是 专栏/会员/大专栏 的id
         * type=4，  拼团
         */
        switch (Integer.parseInt(contentPageBean.getType())) {
            case 2:
                return skipAction(contentPageBean);
            case 3:
                JumpDetail.jumpColumn(mContext, contentPageBean.product_id, "", 3);
                return UrlType.APP_JUMP_OPEN;
            default:
                break;
        }
        return UrlType.WECHAT_OPEN;
    }

    private UrlType skipAction(ContentPageBean contentPageBean) {
        /*
         * resource_type：1-图文，2-音频，3-视频，4-直播，5-报名，7-社群，16-小鹅打卡，20-电子书，21-实物商品
         */
        switch (Integer.parseInt(contentPageBean.getResource_type())) {
            case 0:
                break;
            case 1:
                JumpDetail.jumpImageText(mContext, contentPageBean.getResource_id(), "", "");
                break;
            case 2:
                JumpDetail.jumpAudio(mContext, contentPageBean.getResource_id(), 0);
                break;
            case 3:
                JumpDetail.jumpVideo(mContext, contentPageBean.getResource_id(), "", false, "");
                break;
            default:
//                ToastUtils.show(mContext, R.string.Jump_not_text);
                return UrlType.WECHAT_OPEN;
        }
        return UrlType.APP_JUMP_OPEN;
    }

    private static boolean isXiaoeDomain(String url) {
        for (String domain : XIAOE_DOMAIN) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    public static class ContentPageBean {

        /**
         * channel_id : 85442
         * type : 2
         * resource_type : 1
         * resource_id : i_5c09064de7980_fdS1uUb3
         * product_id :
         * app_id : appe0MEs6qX8480
         */

        private int channel_id;
        /**
         * 页面类型
         */
        private String type;
        /**
         * 资源类型
         */
        private String resource_type;
        /**
         * 资源id
         */
        private String resource_id;
        /**
         * 专栏id
         */
        private String product_id;
        /**
         * 店铺id
         */
        private String app_id;

        public int getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(int channel_id) {
            this.channel_id = channel_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        @Override
        public String toString() {
            return "ContentPageBean{" +
                    "channel_id=" + channel_id +
                    ", type='" + type + '\'' +
                    ", resource_type='" + resource_type + '\'' +
                    ", resource_id='" + resource_id + '\'' +
                    ", product_id='" + product_id + '\'' +
                    ", app_id='" + app_id + '\'' +
                    '}';
        }
    }

}

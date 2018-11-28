package com.xiaoe.shop.wxb.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.audio.ui.AudioActivity;
import com.xiaoe.shop.wxb.business.bought_list.ui.BoughtListActivity;
import com.xiaoe.shop.wxb.business.cdkey.ui.CdKeyActivity;
import com.xiaoe.shop.wxb.business.column.ui.ColumnActivity;
import com.xiaoe.shop.wxb.business.comment.ui.CommentActivity;
import com.xiaoe.shop.wxb.business.coupon.ui.CouponActivity;
import com.xiaoe.shop.wxb.business.coupon.ui.CouponDetailActivity;
import com.xiaoe.shop.wxb.business.course.ui.CourseImageTextActivity;
import com.xiaoe.shop.wxb.business.download.ui.OffLineCacheActivity;
import com.xiaoe.shop.wxb.business.earning.ui.IntegralActivity;
import com.xiaoe.shop.wxb.business.earning.ui.ScholarshipActivity;
import com.xiaoe.shop.wxb.business.earning.ui.WithdrawalActivity;
import com.xiaoe.shop.wxb.business.earning.ui.WithdrawalRecordActivity;
import com.xiaoe.shop.wxb.business.earning.ui.WithdrawalResultActivity;
import com.xiaoe.shop.wxb.business.historymessage.ui.HistoryMessageActivity;
import com.xiaoe.shop.wxb.business.login.ui.LoginActivity;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.business.mine_learning.ui.MineLearningActivity;
import com.xiaoe.shop.wxb.business.navigate_detail.ui.NavigateDetailActivity;
import com.xiaoe.shop.wxb.business.search.ui.SearchActivity;
import com.xiaoe.shop.wxb.business.search.ui.SearchMoreActivity;
import com.xiaoe.shop.wxb.business.setting.ui.SettingAccountActivity;
import com.xiaoe.shop.wxb.business.setting.ui.SettingPersonActivity;
import com.xiaoe.shop.wxb.business.super_vip.ui.SuperVipActivity;
import com.xiaoe.shop.wxb.business.video.ui.VideoActivity;
import com.xiaoe.shop.wxb.common.pay.ui.PayActivity;

public class JumpDetail {
    private static final String TAG = "JumpDetail";

    /**
     * 跳转音频详情
     * @param context
     * @param resId
     * @param hasBuy 0-未购买。1-已购买
     */
    public static void jumpAudio(Context context, String resId, int hasBuy){
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        String resourceId = "";
        if(playEntity != null){
            resourceId = playEntity.getResourceId();
        }
        if(!resourceId.equals(resId)){
            AudioMediaPlayer.stop();

            playEntity = new AudioPlayEntity();
            playEntity.setAppId(Constants.getAppId());
            playEntity.setResourceId(resId);
            playEntity.setIndex(0);
            playEntity.setPlay(true);
            playEntity.setCode(-2);
            playEntity.setHasBuy(hasBuy);
            AudioMediaPlayer.setAudio(playEntity, false);

            AudioPlayUtil.getInstance().refreshAudio(playEntity);
            AudioPresenter audioPresenter = new AudioPresenter(null);
            audioPresenter.requestDetail(resId);
        }
        Intent intent = new Intent(context, AudioActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity)
            ((Activity)context).overridePendingTransition(R.anim.slide_bottom_in,R.anim.slide_top_out);
    }

    /**
     * 跳转专栏详情
     * @param context
     * @param resId
     * @param imageUrl
     * @param resourceType 5 -- 会员，6 -- 专栏； 8 -- 大专栏
     */
    public static void jumpColumn(Context context, String resId,String imageUrl, int resourceType){
        Intent intent = new Intent(context, ColumnActivity.class);
        intent.putExtra("resource_id", resId);
//        intent.putExtra("isBigColumn", isBigColumn);
        intent.putExtra("resource_type", resourceType);
        if(!TextUtils.isEmpty(imageUrl)){
            intent.putExtra("column_image_url", imageUrl);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转视频详情
     * @param context
     * @param resId
     */
    public static void jumpVideo(Context context, String resId, String videoImageUrl, boolean localResource, String columnId){
        XiaoeActivity xiaoeActivity = (XiaoeActivity) context;
//        boolean isReadWrite = true;//是否有读写权限
//        for (String permissions : Constants.READ_WRITE_PERMISSIONS){
//            //检查是否有读写权限
//            if(!Global.g().checkPermissions(permissions)){
//                isReadWrite = false;
//                break;
//            }
//        }
//        if(!isReadWrite){
//            initPermission();
//            return;
//        }

        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("resourceId", resId);
        if(!TextUtils.isEmpty(videoImageUrl)){
            intent.putExtra("videoImageUrl", videoImageUrl);
        }
        intent.putExtra("local_resource", localResource);
        intent.putExtra("columnId", columnId);
        context.startActivity(intent);
    }
    public static void jumpImageText(Context context, String resId, String imageUrl, String columnId){
        Intent intent = new Intent(context, CourseImageTextActivity.class);
        if(!TextUtils.isEmpty(imageUrl)){
            intent.putExtra("imgUrl", imageUrl);
        }
        intent.putExtra("resourceId", resId);
        intent.putExtra("columnId", columnId);
        context.startActivity(intent);
    }

    /**
     * 跳转商品分组
     * @param context
     * @param pageTitle
     * @param resourceId
     */
    public static void jumpShopGroup(Context context, String pageTitle, String resourceId) {
        Intent intent = new Intent(context, NavigateDetailActivity.class);
        intent.putExtra("pageTitle", pageTitle);
        intent.putExtra("resourceId", resourceId);
        context.startActivity(intent);
    }

    public static void jumpComment(Context context, String resourceId, int resourceType, String resourceTitle){
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("resourceId", resourceId);
        intent.putExtra("resourceType", resourceType);
        intent.putExtra("resourceTitle", resourceTitle);
        context.startActivity(intent);
    }

    /**
     * 跳转到主页
     * @param context 上下文
     * @param isFormalUser 是否为正式用户
     */
    public static void jumpMain(Context context, boolean isFormalUser) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("isFormalUser", isFormalUser);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * 跳转到主页的某一个页面
     *
     * @param context      上下文
     * @param isFormalUser
     * @param needChange   是否需要切换
     * @param tabIndex      主页的
     */
    public static void jumpMainScholarship(Context context, boolean isFormalUser, boolean needChange, int tabIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("isFormalUser", isFormalUser);
        intent.putExtra("needChange", needChange);
        intent.putExtra("tabIndex", tabIndex);
        context.startActivity(intent);
    }

    /**
     * 跳转到登录页面
     * @param context 上下文
     */
    public static void jumpLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 游客跳转到登录页面
     * @param context 上下文
     */
    public static void jumpLogin(Context context, boolean isTouristClick) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isTouristClick", isTouristClick);
        context.startActivity(intent);
    }

    /**
     * 跳转到支付
     * @param context
     */
    public static void jumpPay(Context context, String resourceId, int resourceType, String imgUrl, String title, int price, String expireTime){
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("resourceId", resourceId);
        intent.putExtra("resourceType", resourceType);
        intent.putExtra("image_url", imgUrl);
        intent.putExtra("title", title);
        intent.putExtra("price", price);
        intent.putExtra("expireTime", expireTime);
        context.startActivity(intent);
    }

    /**
     * 超级会员跳转支付重载
     * @param context
     * @param resourceId
     * @param productId
     * @param resourceType
     * @param imgUrl
     * @param title
     * @param price
     */
    public static void jumpPay(Context context, String resourceId, String productId, int resourceType, String imgUrl, String title, int price, String expireTime) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("resourceId", resourceId);
        intent.putExtra("productId", productId);
        intent.putExtra("resourceType", resourceType);
        intent.putExtra("image_url", imgUrl);
        intent.putExtra("title", title);
        intent.putExtra("price", price);
        intent.putExtra("expireTime", expireTime);
        context.startActivity(intent);
    }

    /**
     * 跳转个人信息页面
     * @param context 上下文
     * @param avatar 微信头像
     */
    public static void jumpMineMsg(Context context, String avatar) {
        Intent intent = new Intent(context, SettingPersonActivity.class);
        intent.putExtra("avatar", avatar);
        context.startActivity(intent);
    }

    /**
     * 跳转账号设置
     * @param context 上下文
     */
    public static void jumpAccount(Context context) {
        Intent intent = new Intent(context, SettingAccountActivity.class);
        context.startActivity(intent);
    }

    /**
     * 优惠券
     *
     * @param context
     * @param couponInfo
     */
    public static void jumpCouponCanRerource(Context context, CouponInfo couponInfo){
        Intent intent = new Intent(context, CouponDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("coupon_info", couponInfo);
        intent.putExtra("coupon_bundle", bundle);
        context.startActivity(intent);
    }

    /**
     * 跳转到搜索页面
     * @param context
     */
    public static void jumpSearch(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到超级会员页
     * @param context
     */
    public static void jumpSuperVip(Context context) {
        Intent intent = new Intent(context, SuperVipActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到超级会员页，无需购买
     * @param context
     */
    public static void jumpSuperVip(Context context, boolean hasBuy) {
        Intent intent = new Intent(context, SuperVipActivity.class);

        intent.putExtra("hasBuy", hasBuy);

        context.startActivity(intent);
    }

    /**
     * 跳转到我的首页和我正在学页面
     * @param context
     */
    public static void jumpMineLearning(Context context, String pageTitle) {
        Intent intent = new Intent(context, MineLearningActivity.class);
        intent.putExtra("pageTitle", pageTitle);
        context.startActivity(intent);
    }

    /**
     * 跳转到已购列表页面
     * @param context
     */
    public static void jumpBoughtList(Context context, String taskId, boolean isSuperVip) {
        Intent intent = new Intent(context, BoughtListActivity.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("isSuperVip", isSuperVip);
        context.startActivity(intent);
    }

    /**
     * 跳转到积分页面
     * @param context
     */
    public static void jumpIntegralActivity(Context context) {
        Intent intent = new Intent(context, IntegralActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到奖学金页面
     * @param context
     */
    public static void jumpScholarshipActivity(Context context) {
        Intent intent = new Intent(context, ScholarshipActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转提现记录页面
     * @param context
     */
    public static void jumpWrRecord(Context context) {
        Intent intent = new Intent(context, WithdrawalRecordActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转提现页面
     * @param context
     */
    public static void jumpWr(Context context, String allMoney) {
        Intent intent = new Intent(context, WithdrawalActivity.class);
        intent.putExtra("allMoney", allMoney);
        context.startActivity(intent);
    }

    /**
     * 跳转到提现成功页面
     * @param context
     */
    public static void jumpWrResult(Context context, String showPrice) {
        Intent intent = new Intent(context, WithdrawalResultActivity.class);
        intent.putExtra("showPrice", showPrice);
        context.startActivity(intent);
    }

    /**
     * 跳转优惠券列表
     *
     * @param context
     */
    public static void jumpCoupon(Context context) {
        Intent intent = new Intent(context, CouponActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转离线缓存
     *
     * @param context
     */
    public static void jumpOffLine(Context context) {
        Intent intent = new Intent(context, OffLineCacheActivity.class);
        context.startActivity(intent);
    }

    /**
     * 兑换码
     *
     * @param context
     */
    public static void jumpCdKey(Context context) {
        Intent intent = new Intent(context, CdKeyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 历史消息列表
     *
     * @param context
     */
    public static void jumpHistoryMessage(Context context) {
        Intent intent = new Intent(context, HistoryMessageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到搜索更多页面
     * @param context      上下文
     * @param keyword      搜索关键词
     * @param resourceType 搜索资源类型
     */
    public static void jumpSearchMore(Context context, String keyword, int resourceType) {
        Intent intent = new Intent(context, SearchMoreActivity.class);

        intent.putExtra("keyword", keyword);
        intent.putExtra("resourceType", resourceType);

        context.startActivity(intent);
    }
}

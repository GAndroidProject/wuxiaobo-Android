package xiaoe.com.shop.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.CouponInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.audio.presenter.AudioPresenter;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
import xiaoe.com.shop.business.bought_list.ui.BoughtListActivity;
import xiaoe.com.shop.business.cdkey.ui.CdKeyActivity;
import xiaoe.com.shop.business.column.ui.ColumnActivity;
import xiaoe.com.shop.business.comment.ui.CommentActivity;
import xiaoe.com.shop.business.coupon.ui.CouponActivity;
import xiaoe.com.shop.business.coupon.ui.CouponDetailActivity;
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.business.download.ui.OffLineCacheActivity;
import xiaoe.com.shop.business.earning.ui.IntegralActivity;
import xiaoe.com.shop.business.earning.ui.ScholarshipActivity;
import xiaoe.com.shop.business.earning.ui.WithdrawalActivity;
import xiaoe.com.shop.business.earning.ui.WithdrawalRecordActivity;
import xiaoe.com.shop.business.earning.ui.WithdrawalResultActivity;
import xiaoe.com.shop.business.login.ui.LoginActivity;
import xiaoe.com.shop.business.main.ui.MainActivity;
import xiaoe.com.shop.business.mine_learning.ui.MineLearningActivity;
import xiaoe.com.shop.business.navigate_detail.ui.NavigateDetailActivity;
import xiaoe.com.shop.business.search.ui.SearchActivity;
import xiaoe.com.shop.business.setting.ui.SettingAccountActivity;
import xiaoe.com.shop.business.setting.ui.SettingPersonActivity;
import xiaoe.com.shop.business.super_vip.ui.SuperVipActivity;
import xiaoe.com.shop.business.video.ui.VideoActivity;
import xiaoe.com.shop.common.pay.ui.PayActivity;

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
            playEntity.setHasBuy(hasBuy);
        }
        if(!resourceId.equals(resId)){
            AudioMediaPlayer.stop();

            playEntity = new AudioPlayEntity();
            playEntity.setAppId(CommonUserInfo.getShopId());
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
     * @param isBigColumn
     */
    public static void jumpColumn(Context context, String resId,String imageUrl, boolean isBigColumn){
        Intent intent = new Intent(context, ColumnActivity.class);
        intent.putExtra("resource_id", resId);
        intent.putExtra("isBigColumn", isBigColumn);
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
    public static void jumpVideo(Context context, String resId, String videoImageUrl, boolean localResoucrce){
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("resourceId", resId);
        if(!TextUtils.isEmpty(videoImageUrl)){
            intent.putExtra("videoImageUrl", videoImageUrl);
        }
        intent.putExtra("local_resource", localResoucrce);
        context.startActivity(intent);
    }
    public static void jumpImageText(Context context, String resId, String imageUrl){
        Intent intent = new Intent(context, CourseImageTextActivity.class);
        if(!TextUtils.isEmpty(imageUrl)){
            intent.putExtra("imgUrl", imageUrl);
        }
        intent.putExtra("resourceId", resId);
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
     * 跳转到主页的奖学金页面
     * @param context  上下文
     * @param needChange 是否需要切换
     */
    public static void jumpMainScholarship(Context context, boolean needChange) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("needChange", needChange);
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
     * 跳转到支付
     * @param context
     */
    public static void jumpPay(Context context, String resourceId, int resourceType, String imgUrl, String title, int price){
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("resourceId", resourceId);
        intent.putExtra("resourceType", resourceType);
        intent.putExtra("image_url", imgUrl);
        intent.putExtra("title", title);
        intent.putExtra("price", price);
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

    // 跳转优惠券
    public static void jumpCoupon(Context context) {
        Intent intent = new Intent(context, CouponActivity.class);
        context.startActivity(intent);
    }

    // 跳转离线缓存
    public static void jumpOffLine(Context context) {
        Intent intent = new Intent(context, OffLineCacheActivity.class);
        context.startActivity(intent);
    }

    public static void jumpCdKey(Context context) {
        Intent intent = new Intent(context, CdKeyActivity.class);
        context.startActivity(intent);
    }
}

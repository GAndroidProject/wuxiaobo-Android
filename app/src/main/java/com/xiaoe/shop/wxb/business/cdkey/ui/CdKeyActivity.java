package com.xiaoe.shop.wxb.business.cdkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaoe.common.entitys.ExchangeSuccessInfo;
import com.xiaoe.common.entitys.GetSuperMemberSuccessEvent;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.CdKeyRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.cdkey.presenter.CdKeyPresenter;
import com.xiaoe.shop.wxb.business.column.ui.ColumnActivity;
import com.xiaoe.shop.wxb.business.coupon.ui.CouponActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.OSUtils;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CdKeyActivity extends XiaoeActivity {

    private static final String TAG = "CdKeyActivity";

    @BindView(R.id.exchange_tool_bar)
    Toolbar exchangeToolbar;
    @BindView(R.id.cd_key_back)
    ImageView cdBack;
    @BindView(R.id.cd_key_content)
    EditText cdContent;
    @BindView(R.id.cd_key_submit)
    Button cdSubmit;
    @BindView(R.id.cd_error_msg)
    TextView cdErrorMsg;
    Unbinder mUnBinder;

    CustomDialog mProgressDialog;
    AlertDialog mAlertDialog;

    CdKeyPresenter mCdKeyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_cd_key);
        mUnBinder = ButterKnife.bind(this);

        initTitle();
        initListener();
    }

    // 沉浸式初始化
    private void initTitle() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        exchangeToolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCdKeyPresenter == null) {
                mCdKeyPresenter = new CdKeyPresenter(CdKeyActivity.this);
            }
            mCdKeyPresenter.requestData(cdContent.getText().toString());
        }
    };

    @OnClick({R.id.cd_key_back,R.id.cd_key_submit})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.cd_key_back://返回按钮
                onBackPressed();
                break;
            case R.id.cd_key_submit://立即兑换按钮
                showProgressDialog();
                cdSubmit.removeCallbacks(mRunnable);//防止同一时间双次点击，多次请求
                cdSubmit.postDelayed(mRunnable,100);
                break;
            default:
                break;
        }
    }

    /**
     * 请求进度对话框展示
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = getDialog();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        if (!mProgressDialog.isShowing()){
            mProgressDialog.setLoadMessage(getString(R.string.exchanging_text));
            mProgressDialog.showLoadDialog(true);
        }
    }

    /**
     * 请求进度对话框消失
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismissDialog();
    }

    class SuccessDialogViewHold{
        List<ExchangeSuccessInfo.GoodsDataBean> goods;
        List<ExchangeSuccessInfo.CouponDataBean> coupons;

        boolean goodHasNoData;
        boolean couponHasNoData;
        @OnClick({R.id.btn_close,R.id.btn_see})
        public void onClick(View view){
            hideSuccessDialog();
            switch (view.getId()){
                case R.id.btn_close:
                    break;
                case R.id.btn_see:
                    handleUserSeeJump();
                    break;
                default:
                    break;
            }
        }

        /**
         * 处理用户兑换成功点击去查看按钮的跳转
         */
        private void handleUserSeeJump() {
            if (!goodHasNoData){
                ExchangeSuccessInfo.GoodsDataBean good = null;
                boolean isHasSuperMember = false;//是否有超级会员
                for (int i = goods.size() -1 ; i > -1; i--) {
                    good = goods.get(i);
                    if (good != null && 23 == good.getResource_type()){
                        isHasSuperMember = true;
                        break;
                    }
                }
                if (good != null){
                    if (isHasSuperMember){//有超级会员
                        EventBus.getDefault().post(new GetSuperMemberSuccessEvent(true));
                        Toast(getString(R.string.cd_key_super_member));
                        finish();
                    }else {
                        if(good.getResource_type() == ColumnActivity.RESOURCE_TYPE_TOPIC){
                            JumpDetail.jumpColumn(CdKeyActivity.this,good.getResource_id(),
                                    null,8);
                        }else if(good.getResource_type() == ColumnActivity.RESOURCE_TYPE_MEMBER){
                            JumpDetail.jumpColumn(CdKeyActivity.this,good.getResource_id(),
                                    null,5);
                        }else if(good.getResource_type() == ColumnActivity.RESOURCE_TYPE_COLUMN) {
                            JumpDetail.jumpColumn(CdKeyActivity.this,good.getResource_id(),
                                    null,6);
                        }else if(good.getResource_type() == 1){
                            JumpDetail.jumpImageText(CdKeyActivity.this, good.getResource_id(), "", "");
                        }else if(good.getResource_type() == 2){
                            JumpDetail.jumpAudio(CdKeyActivity.this, good.getResource_id(), 1);
                        }else if(good.getResource_type() == 3){
                            JumpDetail.jumpVideo(CdKeyActivity.this, good.getResource_id(), "", false, "");
                        }else{
                            ToastUtils.show(mContext, R.string.unknown_type);
                        }

                    }
                }

            }else if (!couponHasNoData){
                Intent intent = new Intent(CdKeyActivity.this, CouponActivity.class);
                startActivity(intent);
            }
        }

        public SuccessDialogViewHold(View view,List<ExchangeSuccessInfo.GoodsDataBean> goods,
                                     List<ExchangeSuccessInfo.CouponDataBean> coupons,
                                     boolean goodHasNoData,boolean couponHasNoData){
            ButterKnife.bind(this,view);
            this.goods = goods;
            this.coupons = coupons;
            this.goodHasNoData = goodHasNoData;
            this.couponHasNoData = couponHasNoData;
        }
    }

    /**
     * 兑换成功对话框展示
     */
    private void showSuccessDialog(List<ExchangeSuccessInfo.GoodsDataBean> goods,List<ExchangeSuccessInfo.CouponDataBean> coupons) {
        boolean goodHasNoData = goods == null || 0 == goods.size();
        boolean couponHasNoData = coupons == null || 0 == coupons.size();
        if (goodHasNoData && couponHasNoData)   return;

        if (mAlertDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            mAlertDialog = builder.create();
        }
        if (!mAlertDialog.isShowing())  mAlertDialog.show();

        mAlertDialog.getWindow().setLayout(Dp2Px2SpUtil.dp2px(this,240),
                Dp2Px2SpUtil.dp2px(this,230));
        mAlertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_layout_dialog2);
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exchange_success,null,
                false);
        SuccessDialogViewHold successDialogViewHold = new SuccessDialogViewHold(view,goods,coupons,goodHasNoData,couponHasNoData);
        mAlertDialog.setContentView(view);

    }

    /**
     * 兑换成功对话框消失
     */
    private void hideSuccessDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
    }

    private void initListener() {
        cdContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    showErrorMsg("");
                    cdSubmit.setEnabled(s.length() > 0);
                    int maxLength = 8;
                    if (s.length() > maxLength){
                        String content = s.toString().substring(0,maxLength);
                        cdContent.setText(content);
                        cdContent.setSelection(content.length());
                        if (!OnClickEvent.onMoreAction(OnClickEvent.DEFAULT_SECOND)) {
                            Toast(String.format(getString(R.string.cd_key_limit_8), maxLength));
                        }
                    }
                    float size = s.length() > 0 ? 32 : 20;
                    cdContent.setTextSize(size);
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        hideProgressDialog();
        if(activityDestroy || !(iRequest instanceof CdKeyRequest)){
            return;
        }
        handleData(success, entity);
    }

    /**
     * 处理兑换请求回来的数据
     * @param success
     * @param entity
     */
    private void handleData(boolean success, Object entity) {
        if (success && entity != null){
            Log.d(TAG, "onMainThreadResponse: " + success + "-- entity = " + entity.toString());
            ExchangeSuccessInfo result = JSON.parseObject(entity.toString(),ExchangeSuccessInfo.class);
            if (result == null)   return;
            if(result.getCode() == NetworkCodes.CODE_SUCCEED){//兑换成功
                if (cdContent != null)  cdContent.setText("");//重置输入框内容
                OSUtils.hideKeyboard(this,cdContent);
                ExchangeSuccessInfo.Data data = result.data;
                if (data != null){
                    List<ExchangeSuccessInfo.GoodsDataBean> goods = data.getGoods_data();
                    List<ExchangeSuccessInfo.CouponDataBean> coupons = data.getCoupon_data();
                    showSuccessDialog(goods,coupons);
                }
            }else {
                String msg = result.getMsg();
                if (!TextUtils.isEmpty(msg)) {
                    if (msg.indexOf("hint") > 0){
                        msg = msg.substring(0,msg.indexOf("hint"));
                    }
                    showErrorMsg(msg);
//                        Toast(msg);
                }
            }
        }else {
            Toast(getString(R.string.cd_key_fail_msg));
        }
    }

    private void showErrorMsg(String msg) {
        if (cdErrorMsg != null) {
            cdErrorMsg.setText(msg);
        }
    }
}

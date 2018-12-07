package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class StatusPagerView extends FrameLayout {
    private static final String TAG = "LoadingView";
    public static final int LOADING = 10001;
    public static final int FINISH = 10002;
    public static final int FAIL = 10003;
    public static final int EMPTY = 10004;
    public static final int SOLD = 10005;

    public static final String FAIL_CONTENT = XiaoeApplication.applicationContext.getString(R.string.request_fail);

    private View rootView;
    private RelativeLayout loadingWrap;
    private SimpleDraweeView loadingState;
    private TextView stateText;
    private ImageView stateImage;
    private int state = LOADING;

    public static final int DETAIL_NONE = R.mipmap.error_page;
    private TextView btnGoTo;

    public StatusPagerView(@NonNull Context context) {
        this(context,null);
    }

    public StatusPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_status_pager, this, true);
        loadingWrap = (RelativeLayout) rootView.findViewById(R.id.pager);
        loadingState = (SimpleDraweeView) rootView.findViewById(R.id.loading_state);
        setLoadingState(View.GONE);

        String url = "res:///" + R.drawable.page_loading;
        SetImageUriUtil.setGifURI(loadingState, url, Dp2Px2SpUtil.dp2px(context, 116), Dp2Px2SpUtil.dp2px(context, 116));

        stateText = (TextView) rootView.findViewById(R.id.state_text);
        stateImage = (ImageView) rootView.findViewById(R.id.state_image);
        setHintStateVisibility(View.GONE);
        btnGoTo = (TextView) rootView.findViewById(R.id.btn_go_to);

//        stateImage.setOnClickListener(view -> {
//            if (onRefreshEvent != null) {
//                onRefreshEvent.onReLoading(state);
//            }
//        });
    }

    public void setLoadingState(int visibility){
        loadingState.setVisibility(visibility);
    }

    public void stateImageWH(int w, int h){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) stateImage.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        stateImage.setLayoutParams(layoutParams);
    }

    public void setHintStateVisibility(int visibility){
        stateImage.setVisibility(visibility);
        stateText.setVisibility(visibility);
    }
    public void setStateImage(int resId){
        stateImage.setImageResource(resId);
    }

    public void setStateText(String text){
        stateText.setText(text);
    }

    public void setBtnGoToText(String text){
        btnGoTo.setText(text);
        if(TextUtils.isEmpty(text)){
            btnGoTo.setVisibility(GONE);
        }else{
            btnGoTo.setVisibility(VISIBLE);
        }
    }

    public void setBtnGoToOnClickListener(OnClickListener listener){
        btnGoTo.setOnClickListener(listener);
    }

    // 加载完成
    public void setLoadingFinish() {
        this.state = FINISH;
        if (loadingState.getVisibility() == View.VISIBLE) {
            loadingState.setVisibility(View.GONE);
        }
        setVisibility(View.GONE);
    }

    // 加载异常情况显示
    public void setPagerState(int state, String hintText, int imageResourceId){
        setVisibility(View.VISIBLE);
        this.state = state;
        if(state == LOADING){
            setLoadingState(View.VISIBLE);
            setHintStateVisibility(View.GONE);
        }else if (state == FAIL || state == SOLD){
            setLoadingState(View.GONE);
            setHintStateVisibility(View.VISIBLE);
            setStateImage(imageResourceId);
            if (state == SOLD) {
                rootView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            stateText.setTextColor(getResources().getColor(R.color.error_hint_color));
            setStateText(hintText);
        }else if (state == EMPTY){
            setLoadingState(View.GONE);
            setHintStateVisibility(View.VISIBLE);
            setStateImage(imageResourceId);
            setStateText(hintText);
        }
    }

    public int getCurrentLoadingStatus() {
        return state;
    }

    private OnRefreshEvent onRefreshEvent;

    public void setOnRefreshEvent(OnRefreshEvent onRefreshEvent) {
        this.onRefreshEvent = onRefreshEvent;
    }

    interface OnRefreshEvent {
        /**
         * 点击重新加载回调
         *
         * @param state
         */
        void onReLoading(int state);
    }
}

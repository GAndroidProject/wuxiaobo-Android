package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pnikosis.materialishprogress.ProgressWheel;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class StatusPagerView extends FrameLayout {
    private static final String TAG = "LoadingView";
    public static final int LOADING = 10001;
    public static final int FINISH = 10002;
    public static final int FAIL = 10003;
    public static final int EMPTY = 10004;
    private View rootView;
    private SimpleDraweeView loadingState;
    private TextView stateText;
    private ImageView stateImage;

    public static final int DETAIL_NONE = R.mipmap.detail_none;
    private TextView btnGoTo;

    public StatusPagerView(@NonNull Context context) {
        this(context,null);
    }

    public StatusPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_status_pager, this, true);
        loadingState = (SimpleDraweeView) rootView.findViewById(R.id.loading_state);
        setLoadingState(View.GONE);

        String url = "res:///" + R.drawable.page_loading;
        SetImageUriUtil.setGifURI(loadingState, url, Dp2Px2SpUtil.dp2px(context, 180), Dp2Px2SpUtil.dp2px(context, 180));

        stateText = (TextView) rootView.findViewById(R.id.state_text);
        stateImage = (ImageView) rootView.findViewById(R.id.state_image);
        setHintStateVisibility(View.GONE);
        btnGoTo = (TextView) rootView.findViewById(R.id.btn_go_to);

        rootView.findViewById(R.id.pager).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setLoadingState(int visibility){
        loadingState.setVisibility(visibility);
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


    public void setPagerState(int state, String hintText, int imageResourceId){
        if(state == FINISH){
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        if(state == LOADING){
            setLoadingState(View.VISIBLE);
            setHintStateVisibility(View.GONE);
        }else if (state == FAIL){
            setLoadingState(View.GONE);
            setHintStateVisibility(View.GONE);
            setStateImage(imageResourceId);
            setStateText(hintText);
        }else if (state == EMPTY){
            setLoadingState(View.GONE);
            setHintStateVisibility(View.GONE);
            setStateImage(imageResourceId);
            setStateText(hintText);
        }
    }
}

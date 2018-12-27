package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.mine.presenter.MineLearningListAdapter;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class MineLearningWrapView extends LinearLayout {

    private Context mContext;

    // 查看全部
    private TextView learningMore;
    // 我正在学图标
    private SimpleDraweeView learningIcon;
    // 我正在学标题
    private TextView learningTitle;
    // 我正在学更新
    private TextView learningUpdate;
    // 我正在学容器
    private RelativeLayout learningContainer;
    // 登录描述
    private TextView learningLoginDesc;
    // 我正在学列表
    private ListView learningList;
    private SimpleDraweeView learningItemAudioIcon;
    private SimpleDraweeView learningItemAudioBgIcon;
    private View learningItemAudio;

    public MineLearningWrapView(Context context) {
        super(context);
        init(context);
    }

    public MineLearningWrapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MineLearningWrapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MineLearningWrapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.mine_learning_wrap, this);
        learningMore = (TextView) view.findViewById(R.id.learning_more);
        learningIcon = (SimpleDraweeView) view.findViewById(R.id.learning_item_icon);
        learningTitle = (TextView) view.findViewById(R.id.learning_item_title);
        learningUpdate = (TextView) view.findViewById(R.id.learning_item_update);
        learningContainer = (RelativeLayout) view.findViewById(R.id.learning_item_container);
        learningLoginDesc = (TextView) view.findViewById(R.id.learning_login_desc);
        learningList = (ListView) view.findViewById(R.id.learning_list);

        learningItemAudio = view.findViewById(R.id.learning_item_audio_item);
        learningItemAudioIcon = (SimpleDraweeView)view.findViewById(R.id.learning_item_audio_icon);
        learningItemAudioBgIcon = (SimpleDraweeView)view.findViewById(R.id.learning_item_audio_icon_bg);
    }

    public void setLearningMoreVisibility(int visibility) {
        learningMore.setVisibility(visibility);
    }

    public void setLearningMoreClickListener(OnClickListener listener) {
        learningMore.setOnClickListener(listener);
    }

    public void setLearningIconURI(String urlString,String type) {
        boolean isAudio = DecorateEntityType.AUDIO.equals(type);
        learningIcon.setVisibility(isAudio ? INVISIBLE : VISIBLE);
        learningItemAudio.setVisibility(isAudio ? VISIBLE : GONE);
        if (isAudio){
            SetImageUriUtil.setImgURI(learningItemAudioBgIcon, "res:///" +  R.mipmap.audio_list_bg ,
                    Dp2Px2SpUtil.dp2px(mContext, 160),Dp2Px2SpUtil.dp2px(mContext, 120));
            String url = TextUtils.isEmpty(urlString) ? "res:///" + R.mipmap.detail_disk : urlString;
            int imageWidthDp = 84;
            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                SetImageUriUtil.setImgURI(learningItemAudioIcon, url, Dp2Px2SpUtil.dp2px(mContext, imageWidthDp),
                        Dp2Px2SpUtil.dp2px(mContext, imageWidthDp));
            } else {// 网络图片
                SetImageUriUtil.setRoundAsCircle(learningItemAudioIcon, Uri.parse(url));
            }
        }else{
            SetImageUriUtil.setImgURI(learningIcon, urlString, Dp2Px2SpUtil.dp2px(mContext, 160), Dp2Px2SpUtil.dp2px(mContext, 120));
        }
    }

    public void setLearningTitle(String title) {
        learningTitle.setText(title);
    }

    public void setLearningUpdate(String update) {
        learningUpdate.setText(update);
    }

    public void setLearningContainerVisibility(int visibility) {
        learningContainer.setVisibility(visibility);
    }

    public int getLearningContainerVisibility() {
        return learningContainer.getVisibility();
    }

    public void setLearningContainerClickListener(OnClickListener listener) {
        learningContainer.setOnClickListener(listener);
    }

    public void setLearningLoginDesc(String loginDesc) {
        learningLoginDesc.setText(loginDesc);
    }

    public String getLearningLoginDesc() {
        return learningLoginDesc.getText().toString();
    }

    public void setLearningLoginDescVisibility(int visibility) {
        learningLoginDesc.setVisibility(visibility);
    }

    public int getLearningLoginDescVisibility() {
        return learningLoginDesc.getVisibility();
    }

    public void setLearningListAdapter(MineLearningListAdapter adapter) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int left = Dp2Px2SpUtil.dp2px(mContext, 16);
        int top = 0;
        int right = Dp2Px2SpUtil.dp2px(mContext, 16);
        int bottom = Dp2Px2SpUtil.dp2px(mContext, 24);
        // 需要根据登录描述是否存在确定 listView 的 marginTop
        if (getLearningLoginDescVisibility() == View.GONE) { // 隐藏，marginTop 为 30
            top = Dp2Px2SpUtil.dp2px(mContext, 30);
            layoutParams.setMargins(left, top, right, bottom);
        } else if (getLearningLoginDescVisibility() == View.VISIBLE) { // 可见，marginTop 为 22
            top = Dp2Px2SpUtil.dp2px(mContext, 40);
            layoutParams.setMargins(left, top, right, bottom);
        }
        learningList.setLayoutParams(layoutParams);
        learningList.setAdapter(adapter);
        MeasureUtil.setListViewHeightBasedOnChildren(learningList);
    }

    public void setLearningListItemClickListener(AdapterView.OnItemClickListener listener) {
        learningList.setOnItemClickListener(listener);
    }
}

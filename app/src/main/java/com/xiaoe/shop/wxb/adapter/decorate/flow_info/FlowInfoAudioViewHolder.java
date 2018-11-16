package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

/**
 * 音频 ViewHolder
 */
public class FlowInfoAudioViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_audio_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_audio_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_audio_avatar)
    public SimpleDraweeView flowInfoAvatar;
    @BindView(R.id.flow_info_audio_joined_desc)
    public TextView flowInfoJoinedDesc;
    @BindView(R.id.flow_info_audio_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_audio_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_audio_price)
    public TextView flowInfoPrice;

    public FlowInfoAudioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

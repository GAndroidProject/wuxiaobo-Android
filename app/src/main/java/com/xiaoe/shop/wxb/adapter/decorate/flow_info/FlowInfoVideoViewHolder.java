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
 * 视频 ViewHolder
 */
public class FlowInfoVideoViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_video_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_video_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_video_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_video_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_video_price)
    public TextView flowInfoPrice;

    FlowInfoVideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

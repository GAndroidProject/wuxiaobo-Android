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
 * 图文 ViewHolder
 */
public class FlowInfoImgTextViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_img_text_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_img_text_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_img_text_tag)
    public TextView flowInfoTag;
    @BindView(R.id.flow_info_img_text_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_img_text_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_img_text_price)
    public TextView flowInfoPrice;

    public FlowInfoImgTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}

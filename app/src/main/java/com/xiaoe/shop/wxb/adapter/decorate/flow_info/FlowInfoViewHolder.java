package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class FlowInfoViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_head_learn)
    public LinearLayout flowInfoLearnWrap;
    @BindView(R.id.flow_info_head_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_head_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_head_icon)
    public SimpleDraweeView flowInfoIcon;
    @BindView(R.id.flow_info_head_icon_desc)
    public TextView flowInfoIconDesc;
    @BindView(R.id.flow_info_recycler)
    public RecyclerView flowInfoRecycler;

    public FlowInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.mine_learning.ui.MineLearningActivity;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class FlowInfoViewHolder extends BaseViewHolder {

    Context mContext;

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

    public FlowInfoViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    // 初始化 viewHolder
    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<FlowInfoRecyclerAdapter> flowInfoRecyclerAdapterArr) {
        flowInfoTitle.setText(currentBindComponent.getTitle());
        if (currentBindComponent.getImgUrl() != null) {
            SetImageUriUtil.setImgURI(flowInfoIcon, currentBindComponent.getImgUrl(), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 20));
        }
        flowInfoDesc.setText(currentBindComponent.getDesc());
        flowInfoIconDesc.setText(currentBindComponent.getJoinedDesc());
        if (flowInfoRecycler.getLayoutManager() == null) {
            // 一个 recycler 配一个 manager
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayout.VERTICAL);
            flowInfoRecycler.setLayoutManager(llm);
        }
        // 信息流适配器
        FlowInfoRecyclerAdapter flowInfoRecyclerAdapter;
        if (flowInfoRecyclerAdapterArr.get(currentBindPos) == null) { // 防止重复 new
            flowInfoRecyclerAdapter = new FlowInfoRecyclerAdapter(mContext, currentBindComponent.getFlowInfoItemList());
            flowInfoRecyclerAdapterArr.put(currentBindPos, flowInfoRecyclerAdapter);
        } else {
            flowInfoRecyclerAdapter = flowInfoRecyclerAdapterArr.get(currentBindPos);
        }
        flowInfoRecycler.setAdapter(flowInfoRecyclerAdapter);
        flowInfoLearnWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (currentBindComponent.isFormUser()) {
                    Intent intent = new Intent(mContext, MineLearningActivity.class);
                    intent.putExtra("pageTitle", "我正在学");
                    mContext.startActivity(intent);
                } else {
                    // showTouristDialog();
                }
            }
        });
    }
}

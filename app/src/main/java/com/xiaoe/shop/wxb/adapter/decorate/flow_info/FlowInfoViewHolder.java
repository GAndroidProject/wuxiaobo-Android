package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.utils.LoginDialogUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

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
    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<NewFlowInfoRecyclerAdapter> flowInfoRecyclerAdapterArr) {
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
        NewFlowInfoRecyclerAdapter newFlowInfoRecyclerAdapter;
        if (flowInfoRecyclerAdapterArr.get(currentBindPos) == null) { // 防止重复 new
            newFlowInfoRecyclerAdapter = new NewFlowInfoRecyclerAdapter(mContext, currentBindComponent.getFlowInfoItemList());
            flowInfoRecyclerAdapterArr.put(currentBindPos, newFlowInfoRecyclerAdapter);
        } else {
            newFlowInfoRecyclerAdapter = flowInfoRecyclerAdapterArr.get(currentBindPos);
        }
        flowInfoRecycler.setAdapter(newFlowInfoRecyclerAdapter);
        flowInfoLearnWrap.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                if (currentBindComponent.isFormUser()) {
                    JumpDetail.jumpMineLearning(mContext, mContext.getString(R.string.learning_tab_title));
                } else {
                    LoginDialogUtils.showTouristDialog(mContext);
                }

                EventReportManager.onEvent(mContext, MobclickEvent.TODAY_PURCHASED_BTN_CLICK);
            }
        });
    }
}

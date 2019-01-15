package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import java.util.List;

public class NewFlowInfoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "NewFlowInfoRecycler";

    private Context mContext;
    private List<FlowInfoItem> mItemList;

    public NewFlowInfoRecyclerAdapter(Context context, List<FlowInfoItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_item, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);
        view.setLayoutParams(layoutParams);
        return new FlowInfoItemViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int bindPos = holder.getAdapterPosition();
        FlowInfoItem bindItem = mItemList.get(bindPos);
        FlowInfoItemViewHolder mediaViewHolder = (FlowInfoItemViewHolder) holder;
        mediaViewHolder.initViewHolder(bindItem, bindPos);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
}

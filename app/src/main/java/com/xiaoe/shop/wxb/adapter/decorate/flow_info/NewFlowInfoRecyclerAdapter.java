package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import java.util.List;

public class NewFlowInfoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "NewFlowInfoRecyclerAdapter";

    private Context mContext;
    private List<FlowInfoItem> mItemList;

    public NewFlowInfoRecyclerAdapter(Context context, List<FlowInfoItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            Log.d(TAG, "onCreateViewHolder: flow_info component viewType get -1");
            return null;
        }
        View view;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);
        switch (viewType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
            case DecorateEntityType.FLOW_INFO_TOPIC: // 大专栏/会员
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_item, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoItemViewHolder(mContext, view);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int bindPos = holder.getAdapterPosition();
        final FlowInfoItem bindItem = mItemList.get(bindPos);
        switch (bindItem.getItemType()) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
            case DecorateEntityType.COLUMN: // 专栏
            case DecorateEntityType.TOPIC: // 大专栏
            case DecorateEntityType.MEMBER: // 会员
            case DecorateEntityType.AUDIO: // 音频
            case DecorateEntityType.VIDEO: // 视频
                FlowInfoItemViewHolder mediaViewHolder = (FlowInfoItemViewHolder) holder;
                mediaViewHolder.initViewHolder(bindItem, bindPos);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}

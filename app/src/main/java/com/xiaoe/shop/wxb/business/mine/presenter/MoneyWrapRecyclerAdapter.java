package com.xiaoe.shop.wxb.business.mine.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import com.xiaoe.common.entitys.MineMoneyItemInfo;
import com.xiaoe.common.interfaces.OnItemClickWithMoneyItemListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.events.OnClickEvent;

public class MoneyWrapRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "MoneyWrapRecyclerAdapte";

    private Context mContext;
    private List<MineMoneyItemInfo> mItemList;

    private OnItemClickWithMoneyItemListener onItemClickWithMoneyItemListener;

    public void setOnItemClickWithMoneyItemListener(OnItemClickWithMoneyItemListener listener) {
        this.onItemClickWithMoneyItemListener = listener;
    }

    public MoneyWrapRecyclerAdapter(Activity activity, List<MineMoneyItemInfo> itemList) {
        this.mContext = activity;
        this.mItemList = itemList;
    }

    public MoneyWrapRecyclerAdapter(Context context, List<MineMoneyItemInfo> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.money_item, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int left = Dp2Px2SpUtil.dp2px(parent.getContext(), 25);
        int top = Dp2Px2SpUtil.dp2px(parent.getContext(), 15);
        int right = Dp2Px2SpUtil.dp2px(parent.getContext(), 25);
        int bottom = Dp2Px2SpUtil.dp2px(parent.getContext(), 12);
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
        return new MoneyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int currentPos = holder.getAdapterPosition();
        final MineMoneyItemInfo currentItem = mItemList.get(currentPos);
        MoneyItemViewHolder viewHolder = (MoneyItemViewHolder) holder;

        viewHolder.money_item_title.setText(currentItem.getItemTitle());
        viewHolder.money_item_title2.setVisibility(0 == position ? View.VISIBLE : View.GONE);
        viewHolder.money_item_desc.setText(currentItem.getItemDesc());
        viewHolder.money_item_wrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (onItemClickWithMoneyItemListener != null) {
                    onItemClickWithMoneyItemListener.onMineMoneyItemInfoClickListener(v, currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

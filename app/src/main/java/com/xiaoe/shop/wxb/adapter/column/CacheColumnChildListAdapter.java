package com.xiaoe.shop.wxb.adapter.column;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class CacheColumnChildListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CacheColumnChildListAda";
    private final Context mContext;

    public CacheColumnChildListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_child_cache_column, parent, false);
        return new CacheColumnChildListHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CacheColumnChildListHolder childListHolder = (CacheColumnChildListHolder) holder;
        childListHolder.bindView();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

package com.xiaoe.shop.wxb.business.search.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.xiaoe.common.interfaces.OnItemClickWithPosListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.events.OnClickEvent;

public class RecommendRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder>  {

    private static final String TAG = "RecommendAdapter";

    private Context mContext;
    private List<String> recommendList;

    private OnItemClickWithPosListener onItemClickWithPosListener;

    public RecommendRecyclerAdapter(Context context, List<String> recommendList) {
        this.mContext = context;
        this.recommendList = recommendList;
    }

    public void setOnTabClickListener(OnItemClickWithPosListener onItemClickWithPosListener) {
        this.onItemClickWithPosListener = onItemClickWithPosListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.search_main_item, null);
        return new SearchMainContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        SearchMainContentViewHolder searchMainContentViewHolder = (SearchMainContentViewHolder) holder;
        Log.d(TAG, "onBindViewHolder: position ---- " + position);
        String content = recommendList.get(position);
        searchMainContentViewHolder.content.setText(content);
        final int tempPos = searchMainContentViewHolder.getAdapterPosition();
        searchMainContentViewHolder.content.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (onItemClickWithPosListener != null) {
                    onItemClickWithPosListener.onItemClick(v, tempPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendList == null ? 0 : recommendList.size();
    }
}

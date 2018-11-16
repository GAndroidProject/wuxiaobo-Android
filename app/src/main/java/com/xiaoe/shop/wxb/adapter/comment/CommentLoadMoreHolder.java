package com.xiaoe.shop.wxb.adapter.comment;

import android.view.View;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.widget.ListBottomLoadMoreView;

public class CommentLoadMoreHolder extends BaseViewHolder {

    private final ListBottomLoadMoreView loadMoreView;

    public CommentLoadMoreHolder(View itemView) {
        super(itemView);
        loadMoreView = (ListBottomLoadMoreView) itemView.findViewById(R.id.comment_load_more);
    }
    public void bindView(int state){
        setLoadState(state);
    }

    public void setLoadState(int state){
        loadMoreView.setLoadState(state);
    }

    public int getLoadState(){
        return loadMoreView.getLoadState();
    }
}

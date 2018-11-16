package com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class GraphicNavViewHolder extends BaseViewHolder {

    @BindView(R.id.graphic_nav_recycler)
    public RecyclerView graphicNavRecycler;

    public GraphicNavViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

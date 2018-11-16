package com.xiaoe.shop.wxb.adapter.decorate.search;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class SearchViewHolder extends BaseViewHolder {

    @BindView(R.id.search_wxb)
    public TextView searchWxb;
    @BindView(R.id.search_title)
    public TextView searchTitle;
    @BindView(R.id.search_icon)
    public SimpleDraweeView searchIcon;

    public SearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

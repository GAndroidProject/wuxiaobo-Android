package com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure;

import android.view.View;

import com.youth.banner.Banner;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class ShufflingFigureViewHolder extends BaseViewHolder {

    @BindView(R.id.shuffling_figure)
    public Banner banner;

    public ShufflingFigureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

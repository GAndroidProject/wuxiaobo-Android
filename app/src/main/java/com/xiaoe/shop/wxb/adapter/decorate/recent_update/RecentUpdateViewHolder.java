package com.xiaoe.shop.wxb.adapter.decorate.recent_update;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

/**
 * 最近更新 ViewHolder
 */
public class RecentUpdateViewHolder extends BaseViewHolder {

    @BindView(R.id.recent_update_sub_list)
    public ListView recentUpdateListView;
    @BindView(R.id.recent_update_avatar)
    public SimpleDraweeView recentUpdateAvatar;
    @BindView(R.id.recent_update_sub_title)
    public TextView recentUpdateSubTitle;
    @BindView(R.id.recent_update_sub_desc)
    public TextView recentUpdateSubDesc;
    @BindView(R.id.recent_update_sub_btn)
    public Button recentUpdateSubBtn;

    public RecentUpdateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

package com.xiaoe.shop.wxb.business.setting.presenter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.interfaces.OnItemClickWithPosListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class SettingItemViewHolder extends BaseViewHolder {

    @BindView(R.id.setting_item_container)
    FrameLayout itemContainer;
    @BindView(R.id.item_title)
    TextView itemTitle;
    @BindView(R.id.item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.item_content)
    TextView itemContent;
    @BindView(R.id.item_go)
    ImageView itemGo;

    SettingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

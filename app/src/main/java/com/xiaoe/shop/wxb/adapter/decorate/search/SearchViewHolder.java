package com.xiaoe.shop.wxb.adapter.decorate.search;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class SearchViewHolder extends BaseViewHolder {

    @BindView(R.id.search_wxb)
    public TextView searchWxb;
    @BindView(R.id.search_title)
    public TextView searchTitle;
    @BindView(R.id.search_icon)
    public SimpleDraweeView searchIcon;

    private Context mContext;

    public SearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public SearchViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(ComponentInfo currentBindComponent) {
        String text = "C&nbsp;&nbsp;&nbsp;&nbsp;O&nbsp;&nbsp;&nbsp;&nbsp;U&nbsp;&nbsp;&nbsp;&nbsp;" +
                "R&nbsp;&nbsp;&nbsp;&nbsp;S&nbsp;&nbsp;&nbsp;&nbsp;E";
        searchWxb.setText(Html.fromHtml(text));
        searchTitle.setText(currentBindComponent.getTitle());
        String searchDefault = "res:///" + R.mipmap.class_search;
        SetImageUriUtil.setImgURI(searchIcon, searchDefault, Dp2Px2SpUtil.dp2px(mContext, 375), Dp2Px2SpUtil.dp2px(mContext, 250));
        if (!currentBindComponent.isNeedDecorate()) {
            searchWxb.setVisibility(View.GONE);
        }
        searchIcon.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpSearch(mContext);
            }
        });
    }
}

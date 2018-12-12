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
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class SearchViewHolder extends BaseViewHolder {

    @BindView(R.id.search_title)
    public TextView searchTitle;
//    @BindView(R.id.search_title)
//    public TextView searchTitle;
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
        String text = "C&nbsp;O&nbsp;U&nbsp;R&nbsp;S&nbsp;E";
        searchTitle.setText(Html.fromHtml(text));
//        searchTitle.setText(currentBindComponent.getTitle());
        String searchDefault = "res:///" + R.mipmap.class_search_new;
        SetImageUriUtil.setImgURI(searchIcon, searchDefault, Dp2Px2SpUtil.dp2px(mContext, 375), Dp2Px2SpUtil.dp2px(mContext, 250));
//        if (!currentBindComponent.isNeedDecorate()) {
//            searchTitle.setVisibility(View.GONE);
//        }
        searchIcon.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpSearch(mContext);

                EventReportManager.onEvent(mContext, MobclickEvent.COURSE_SEARCH_BTN_CLICK);
            }
        });
    }
}

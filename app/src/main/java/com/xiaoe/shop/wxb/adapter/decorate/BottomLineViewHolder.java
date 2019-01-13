package com.xiaoe.shop.wxb.adapter.decorate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 下划线
 *
 * @author: zak
 * @date: 2019/1/11
 */
public class BottomLineViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.bottom_wrap)
    LinearLayout bottomLineWrap;
    private FrameLayout.LayoutParams layoutParams;

    public BottomLineViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        ButterKnife.bind(this, itemView);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void initViewHolder(boolean isShow) {
        if (isShow) {
            layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 64), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 22));
            bottomLineWrap.setLayoutParams(layoutParams);
            bottomLineWrap.setVisibility(View.VISIBLE);
        } else {
            layoutParams.setMargins(0, 0, 0, 0);
            bottomLineWrap.setLayoutParams(layoutParams);
            bottomLineWrap.setVisibility(View.GONE);
        }
    }
}

package com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoe.common.utils.Dp2Px2SpUtil;

public class GraphicNavItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public GraphicNavItemDecoration(Context context) {
        margin = Dp2Px2SpUtil.dp2px(context, 16);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = margin;
        outRect.right = margin;
    }
}

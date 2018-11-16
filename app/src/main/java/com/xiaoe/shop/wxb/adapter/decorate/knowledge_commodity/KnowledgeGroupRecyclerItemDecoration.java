package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class KnowledgeGroupRecyclerItemDecoration extends RecyclerView.ItemDecoration {

    // item 间隔
    private int itemSpace;
    // 每行 item 的个数
    private int itemNum;

    /**
     *
     * @param itemSpace item 间隔
     * @param itemNum 每行 item 的个数
     */
    public KnowledgeGroupRecyclerItemDecoration(int itemSpace, int itemNum) {
        this.itemSpace = itemSpace;
        this.itemNum = itemNum;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = itemSpace;
        // 第一个元素的右边和第二个元素的左边增加 itemSpace / 2 的边距，因为实际上两个元素的间距是 itemSpace，
        // 但只改变一边的话另外一个元素会被压缩
        if (parent.getChildLayoutPosition(view) % itemNum == 0) {
            outRect.top = itemSpace;
            outRect.right = itemSpace / 2;
            outRect.bottom = 0;
            outRect.left = 0;
        } else {
            outRect.top = itemSpace;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.left = itemSpace / 2;
        }
    }
}

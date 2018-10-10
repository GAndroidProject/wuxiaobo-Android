package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

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
        if (parent.getChildLayoutPosition(view) % itemNum == 0) {
            outRect.top = itemSpace;
            outRect.right = itemSpace;
            outRect.bottom = 0;
            outRect.left = 0;
        } else {
            outRect.top = itemSpace;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.left = itemSpace;
        }
    }
}

package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xiaoe.com.common.utils.Dp2Px2SpUtil;

public class GraphicNavItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public GraphicNavItemDecoration(Context context) {
        margin = Dp2Px2SpUtil.dp2px(context, 25);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = margin;
    }
}

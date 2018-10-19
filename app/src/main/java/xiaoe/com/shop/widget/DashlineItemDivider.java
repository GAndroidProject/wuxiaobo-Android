package xiaoe.com.shop.widget;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.shop.R;

public class DashlineItemDivider extends RecyclerView.ItemDecoration {
    private static final String TAG = "DashlineItemDivider";
    private int paddingLeft = 0;
    private int paddingRight = 0;
    public DashlineItemDivider(int left, int right){
        paddingLeft = left;
        paddingRight = right;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
//        final int left = parent.getPaddingLeft();
        final int left = paddingLeft;
//        final int right = parent.getWidth() - parent.getPaddingRight();
        final int right = parent.getWidth() - paddingRight;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;

            //绘制虚线
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(XiaoeApplication.getmContext().getResources().getColor(R.color.line));
            Path path = new Path();
            path.moveTo(left, top);
            path.lineTo(right,top);
            PathEffect effects = new DashPathEffect(new float[]{10,5,5,5},2);//此处单位是像素不是dp  注意 请自行转化为dp
            paint.setPathEffect(effects);
            c.drawPath(path, paint);
        }
    }

}
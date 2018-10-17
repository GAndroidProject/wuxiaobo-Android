package xiaoe.com.shop.business.setting.presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;

public class LinearDividerDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "LinearDividerDecoration";

    private Context mContext;
    private Drawable mDivider;

    private int mOrientation;

    /**
     * 分割线缩进值
     */
    private int inset;
    private Paint paint;
    private boolean needMargin = true;

    /**
     * @param context context
     * @param orientation layout的方向
     * @param drawable    引入的drawable的ID
     * @param inset       分割线缩进值
     */
    public LinearDividerDecoration(Context context, int orientation, int drawable, int inset) {
        mContext = context;
        mDivider = context.getResources().getDrawable(drawable);
        this.inset = inset;
        initPaint();
        setOrientation(orientation);
    }

    public LinearDividerDecoration(Context context, int drawable, int inset) {
        mContext = context;
        mDivider = context.getResources().getDrawable(drawable);
        this.inset = inset;
        this.needMargin = false;
        initPaint();
        setOrientation(LinearLayoutManager.VERTICAL);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public void setOrientation(int orientation) {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 第一次的进入的时候才需要画线，之后就不画了
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (needMargin) {
                drawVerticalWithMargin(c, parent);
            } else {
                drawVerticalWithoutMargin(c, parent);
            }
        } else {
            drawHorizontal(c, parent);
        }
    }

    // 不绘制 margin
    private void drawVerticalWithoutMargin(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        // 因为采用了空处理，所以最后两个 item 都不画分割线
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                c.drawRect(left, top, right, bottom, paint);
                mDivider.setBounds(left + inset, top, right - inset, bottom);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    // 绘制 margin
    private void drawVerticalWithMargin(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        // 因为采用了空处理，所以最后两个 item 都不画分割线
        for (int i = 0; i < childCount - 2; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                // 第五个元素需要一个 margin，不画线
                if (i == 4) {
                    continue;
                } else {
                    c.drawRect(left, top, right, bottom, paint);
                    mDivider.setBounds(left + inset, top, right - inset, bottom);
                }
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childPosition = parent.getChildAdapterPosition(view);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (childPosition == 0) {
                // 第一个元素需要一个顶部 margin
                outRect.set(0, mDivider.getIntrinsicHeight() + Dp2Px2SpUtil.dp2px(mContext, 12), 0, mDivider.getIntrinsicHeight());
            } else if ( childPosition == 4) {
                if (!needMargin) {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                    return;
                }
                // 第五个元素需要一个底部 margin
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight() + Dp2Px2SpUtil.dp2px(mContext, 12));
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}

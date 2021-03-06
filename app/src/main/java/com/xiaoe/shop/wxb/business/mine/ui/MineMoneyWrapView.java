package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.mine.presenter.MoneyWrapRecyclerAdapter;

public class MineMoneyWrapView extends FrameLayout {

    private static final String TAG = "MineMoneyWrap";

    private Context mContext;
    private RecyclerView moneyRecycler;

    public MineMoneyWrapView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MineMoneyWrapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MineMoneyWrapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MineMoneyWrapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.money_wrap, this);
        moneyRecycler = (RecyclerView) view.findViewById(R.id.mine_money_recycler);
    }

    public void setMoneyRecyclerAdapter(MoneyWrapRecyclerAdapter adapter) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        moneyRecycler.setLayoutManager(gridLayoutManager);
        moneyRecycler.setNestedScrollingEnabled(false);
        MoneyDividerItemDecoration moneyDividerItemDecoration = new MoneyDividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL, R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(mContext, 20));
        moneyRecycler.addItemDecoration(moneyDividerItemDecoration);
        moneyRecycler.setAdapter(adapter);
    }

    class MoneyDividerItemDecoration extends RecyclerView.ItemDecoration{

        private Drawable mDivider;
        private int mOrientation;

        // 分割线缩进值
        private int inset;
        private Paint mPaint;

        MoneyDividerItemDecoration(Context context, int orientation, int drawable, int inset) {
            mDivider = context.getResources().getDrawable(drawable);
            this.inset = inset;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(context.getResources().getColor(R.color.tree_item_border));
            mPaint.setStyle(Paint.Style.FILL);
            setOrientation(orientation);
        }

        private void setOrientation(int orientation) {
            if (orientation != GridLayoutManager.HORIZONTAL && orientation != GridLayoutManager.VERTICAL) {
                throw new IllegalArgumentException("并不是 GridLayoutManager 的方向值...");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            if (mOrientation == GridLayoutManager.HORIZONTAL) {
                drawHorizontal(c, parent);
            } else {
                drawVertical(c, parent);
            }
        }

        /**
         * gridLayoutManager 绘制水平方向的分割线
         * @param canvas 画布
         * @param parent recyclerView
         */
        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();
            // parent.getChildCount() // 这个方法可以获取 RecyclerView 的子 View 的个数，有了这个总数可以循环给每一个子 view 绘制，但是这里只有两个，所以直接选第一个进行绘制
            View child = parent.getChildAt(0);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                mDivider.setBounds(left, top + inset, right, bottom - inset);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(canvas);
        }

        /**
         * 绘制 gridLayoutManager 绘制垂直方向的分割线
         * @param canvas 画布
         * @param parent recyclerView
         */
        private void drawVertical(Canvas canvas, RecyclerView parent) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            View child = parent.getChildAt(0);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                canvas.drawRect(left, top, right, bottom, mPaint);
                mDivider.setBounds(left + inset, top, right - inset, bottom);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(canvas);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (mOrientation == GridLayoutManager.HORIZONTAL) {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}

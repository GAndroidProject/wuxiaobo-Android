package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.interfaces.IonSlidingButtonListener;

/**
 * Created by gyq on 2017/3/27 17:01
 */
public class SlidingButtonView extends HorizontalScrollView {
    private RelativeLayout mDelete;

    private int mScrollWidth;

    private IonSlidingButtonListener mIonSlidingButtonListener;

    private Boolean isOpen = false;
    private Boolean once = false;
    private boolean delete = true;


    public SlidingButtonView(Context context) {
        this(context, null);
    }

    public SlidingButtonView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidingButtonView, 0, 0);
        delete = array.getBoolean(R.styleable.SlidingButtonView_sliding_delete, true);
        array.recycle();
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!once){
            mDelete = (RelativeLayout) findViewById(R.id.btn_delete);
            once = true;
        }
        if(!delete){
            mDelete.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!delete){
            return;
        }
        if(changed){
//            this.scrollTo(0,0);
//            //获取水平滚动条可以滑动的范围，即右侧按钮的宽度
//            mScrollWidth = mDelete.getWidth();
            // Log.i("asd", "mScrollWidth:" + mScrollWidth);
        }
        this.scrollTo(0,0);
        //获取水平滚动条可以滑动的范围，即右侧按钮的宽度
        mScrollWidth = mDelete.getWidth();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!delete){
            return  super.onTouchEvent(ev);
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIonSlidingButtonListener.onDownOrMove(this);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeScrollx();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(!delete){
            return;
        }
        mDelete.setTranslationX(l - mScrollWidth);
    }

    /**
     * 按滚动条被拖动距离判断关闭或打开菜单
     */
    public void changeScrollx(){
        if(getScrollX() >= (mScrollWidth/2)){
            this.smoothScrollTo(mScrollWidth, 0);
            isOpen = true;
            mIonSlidingButtonListener.onMenuIsOpen(this);
        }else{
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }


    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
        if (!isOpen){
            return;
        }
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }




    public void setSlidingButtonListener(IonSlidingButtonListener listener){
        mIonSlidingButtonListener = listener;
    }
}

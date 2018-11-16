package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

import com.xiaoe.shop.wxb.R;

/**
 * 下拉组件放大 View
 */
public class PushScrollView extends ScrollView {

    private static final String TAG = "PushScrollView";

    private View zoomView;
    /** 记录上次事件的Y轴*/
    private float mLastMotionY;
    /** 记录一个滚动了多少距离，通过这个来设置缩放*/
    private int allScroll = -1;
    /** 控件原本的高度*/
    private int height = 0;
    /** 被放大的控件id*/
    private int zoomId;
    /** 最大放大多少像素*/
    private int maxZoom;
    /** 滚动监听*/
    private ScrollViewListener scrollViewListener = null;
    private Handler handler = new PushScrollViewHandler(this);

    private int loadHeight;
    private int loadState = ListBottomLoadMoreView.STATE_NOT_LOAD;

    public PushScrollView(Context context) {
        super(context);
    }

    public PushScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PushScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PushScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        zoomView = findViewById(zoomId);
    }

    private void init(AttributeSet attrs){
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.PushScrollView);
        zoomId = t.getResourceId(R.styleable.PushScrollView_scrollId,-1);
        maxZoom = t.getDimensionPixelOffset(R.styleable.PushScrollView_maxZoom,0);
        t.recycle();
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(zoomView == null || maxZoom == 0){
            return super.dispatchTouchEvent(event);
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if(allScroll != -1){
                handler.sendEmptyMessageDelayed(1,10);
            }
            return super.dispatchTouchEvent(event);
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final float y = event.getY();
                final float diff, absDiff;
                diff = y - mLastMotionY;
                mLastMotionY = y;
                absDiff = Math.abs(diff);
                if(allScroll >= 0 && absDiff > 1){
                    allScroll += diff;

                    if(allScroll < 0){
                        allScroll = 0;
                    }else if(allScroll > maxZoom){
                        allScroll = maxZoom;
                    }
                    ViewGroup.LayoutParams lp = zoomView.getLayoutParams();
                    lp.height = height + allScroll/2;
                    zoomView.setLayoutParams(lp);
                    if(allScroll == 0){
                        allScroll = -1;
                    }
                    return false;
                }
                if (isReadyForPullStart()) {
                    if (absDiff > 0 ) {
                        if (diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            allScroll = 0;
                            height = zoomView.getHeight();
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(allScroll != -1){
            Log.i("ScrollView","onTouchEvent");
            return false;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @return 返回是否可以开始放大
     */
    protected boolean isReadyForPullStart() {
        return getScrollY() == 0 && allScroll < maxZoom;
    }


    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
        }
        if (y > 0) { // 只有上滑才会触发
            int height = getHeight();
            int childHeight = getChildAt(0).getHeight();
            boolean loadMore = y + height >= childHeight - loadHeight;
            if(loadMore && loadState == ListBottomLoadMoreView.STATE_NOT_LOAD && scrollViewListener != null){
                scrollViewListener.onLoadState(loadState);
            }
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY > 0) {
            int height = getHeight();
            int childHeight = getChildAt(0).getHeight();
            boolean loadMore = scrollY + height >= childHeight - loadHeight;
            if(loadMore && loadState == ListBottomLoadMoreView.STATE_NOT_LOAD && scrollViewListener != null){
                scrollViewListener.onLoadState(loadState);
            }
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(PushScrollView scrollView, int x, int y, int oldX, int oldY);
        void onLoadState(int state);
    }

    private static class PushScrollViewHandler extends Handler {
        private WeakReference<PushScrollView> weakReference;

        PushScrollViewHandler(PushScrollView pushScrollView) {
            weakReference = new WeakReference<>(pushScrollView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PushScrollView pushScrollView = weakReference.get();
            pushScrollView.allScroll -= 60;
            if(pushScrollView.allScroll < 0){
                pushScrollView.allScroll = 0;
            }
            ViewGroup.LayoutParams lp = pushScrollView.zoomView.getLayoutParams();
            lp.height = pushScrollView.height + pushScrollView.allScroll / 2;
            pushScrollView.zoomView.setLayoutParams(lp);
            if(pushScrollView.allScroll != 0){
                sendEmptyMessageDelayed(1,10);
            }else{
                pushScrollView.allScroll = -1;
            }
        }
    }
}
